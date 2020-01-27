package com.manorath.csye6225.service;

import ch.qos.logback.classic.spi.IThrowableProxy;
import com.manorath.csye6225.exception.BillDoesNotExistException;
import com.manorath.csye6225.exception.BillDoesNotMatchException;
import com.manorath.csye6225.exception.GeneralExceptionHandler;
import com.manorath.csye6225.model.Bill;
import com.manorath.csye6225.model.User;
import com.manorath.csye6225.repository.BillRepository;
import com.manorath.csye6225.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BillService extends GeneralExceptionHandler {

    @Autowired
    UserRepository userRepository;
    @Autowired
    BillRepository billRepository;

    public BillService() {}

    //Buisness Logic
    public Bill createBill(Bill bill,String email) {
        User u = userRepository.findUserByEmail(email);
        bill.setCreatedTs(new Date());
        bill.setOwnerID(u.getId());
        bill.setUpdatedTs(new Date());
        return billRepository.save(bill);
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
        billRepository.delete(bill);
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
            bill = b.get();
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

        return billRepository.save(updatedBill);
    }

    public List<Bill>  findBillByUser(String email) {
        User u = userRepository.findUserByEmail(email);
        return billRepository.findAllByOwnerID(u.getId());
    }
}
