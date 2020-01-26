package com.manorath.csye6225.service;

import com.manorath.csye6225.exception.GeneralExceptionHandler;
import com.manorath.csye6225.model.Bill;
import com.manorath.csye6225.model.User;
import com.manorath.csye6225.repository.BillRepository;
import com.manorath.csye6225.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

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

    public void deleteBill(String id) {
        billRepository.deleteById(id);
    }

    public List<Bill>  findBillByUser(String email) {
        User u = userRepository.findUserByEmail(email);
        return billRepository.findAllByOwnerID(u.getId());
    }
}
