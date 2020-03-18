package com.manorath.csye6225.service;

import ch.qos.logback.classic.spi.IThrowableProxy;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.manorath.csye6225.exception.BillDoesNotExistException;
import com.manorath.csye6225.exception.BillDoesNotMatchException;
import com.manorath.csye6225.exception.GeneralExceptionHandler;
import com.manorath.csye6225.model.Bill;
import com.manorath.csye6225.model.User;
import com.manorath.csye6225.repository.BillRepository;
import com.manorath.csye6225.repository.UserRepository;
import com.timgroup.statsd.StatsDClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BillService extends GeneralExceptionHandler {

    @Autowired
    UserRepository userRepository;
    @Autowired
    BillRepository billRepository;

    @Autowired
    private StatsDClient statsd;
    
    @Value("${amazon.s3.bucket}")
    String bucketName;

    private AmazonS3 s3Client;

    public BillService() {}

    @PostConstruct
    private void buildAmazon() {
        this.s3Client = AmazonS3ClientBuilder.standard().build();
    }

    //Buisness Logic
    public Bill createBill(Bill bill,String email) {
        User u = userRepository.findUserByEmail(email);
        bill.setCreatedTs(new Date());
        bill.setOwnerID(u.getId());
        bill.setUpdatedTs(new Date());
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Bill savedBill = billRepository.save(bill);
        stopWatch.stop();
        statsd.recordExecutionTime("BillDbCreate",stopWatch.getLastTaskTimeMillis());
        return savedBill;
    }

    public int deleteBill(String email, String id) {
        User u = userRepository.findUserByEmail(email);
        Optional<Bill> b = billRepository.findById(id);
        Bill bill;
        if(!b.isPresent()) {
            return 2;
        }
        else{
            bill = b.get();
        }
        if(!u.getId().equals(bill.getOwnerID())){
            return 1;
        }
        StopWatch stopWatch1 = new StopWatch();
        stopWatch1.start();
        s3Client.deleteObject(bucketName,bill.getAttachment().getUrl());
        stopWatch1.stop();
        statsd.recordExecutionTime("BillS3Delete",stopWatch1.getLastTaskTimeMillis());
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        billRepository.delete(bill);
        stopWatch.stop();
        statsd.recordExecutionTime("BillDbDelete",stopWatch.getLastTaskTimeMillis());
        return 0;
    }

    public Bill getBillById(String email, String id) {
        User u = userRepository.findUserByEmail(email);
        Optional<Bill> b = billRepository.findById(id);
        Bill bill;
        if(!b.isPresent()) {
            throw new BillDoesNotExistException("bill does not exist");
        }
        else{
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            bill = b.get();
            stopWatch.stop();
            statsd.recordExecutionTime("BillDbGet",stopWatch.getLastTaskTimeMillis());
        }
        if(!u.getId().equals(bill.getOwnerID())){
            throw new BillDoesNotMatchException("bill does not match");
        }
        return bill;
    }

    public Bill updateBill(String email,String id,Bill updatedBill) {
        User u = userRepository.findUserByEmail(email);
        Optional<Bill> b = billRepository.findById(id);
        Bill bill;
        if(!b.isPresent()) {
            throw new BillDoesNotExistException("bill does not exist");
        }
        else{
            bill = b.get();
        }
        if(!u.getId().equals(bill.getOwnerID())){
            throw new BillDoesNotMatchException("bill does not match");
        }
        updatedBill.setUpdatedTs(new Date());
        updatedBill.setOwnerID(bill.getOwnerID());
        updatedBill.setCreatedTs(bill.getCreatedTs());

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Bill savedBill = billRepository.save(updatedBill);
        stopWatch.stop();
        statsd.recordExecutionTime("BillDbUpdate",stopWatch.getLastTaskTimeMillis());

        return savedBill;
    }

    public List<Bill>  findBillByUser(String email) {
        User u = userRepository.findUserByEmail(email);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<Bill> bills = billRepository.findAllByOwnerID(u.getId());
        stopWatch.stop();
        statsd.recordExecutionTime("BillFindAll",stopWatch.getLastTaskTimeMillis());
        return bills;
    }
}
