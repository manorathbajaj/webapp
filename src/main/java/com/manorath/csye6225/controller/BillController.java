package com.manorath.csye6225.controller;

import com.manorath.csye6225.exception.*;
import com.manorath.csye6225.model.Bill;
import com.manorath.csye6225.service.BillService;
import com.manorath.csye6225.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
public class BillController extends GeneralExceptionHandler {

    @Autowired
    BillService billService;

    @RequestMapping(value = "v1/bill/",
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public Bill createBill(@Valid @RequestBody Bill bill,
                             HttpServletResponse response, @RequestHeader(value = "Authorization")String auth) {
        String creds[] = Utils.decode(auth);
        if(bill.getId()!= null || bill.getOwnerID()!= null || bill.getCreatedTs()!= null || bill.getUpdatedTs() != null)
        {
            throw new FiledNotAllowedException("Fields not allowed");
        }
        Bill b = billService.createBill(bill,creds[0]);
        if(b == null) {
            throw new PasswordNotValidException("Please enter a valid password");
        }
        response.setHeader("description","Bill created");
        return bill;
    }

    @RequestMapping(value = "v1/bills",
            method = RequestMethod.GET,
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public List<Bill> getAllBills(HttpServletResponse response, @RequestHeader(value = "Authorization")String auth) {
        String creds[] = Utils.decode(auth);
        return billService.findBillByUser(creds[0]);
    }

    @DeleteMapping("v1/bill/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBill(@PathVariable(value = "id") String billId, @RequestHeader(value = "Authorization")String auth) {
        String[] cred = Utils.decode(auth);
        switch (billService.deleteBill(cred[0],billId)) {
            case 1 :
                throw new BillDoesNotMatchException("bill does not match");
            case 2:
                throw new BillDoesNotExistException("bill does not exist");
             default:
                 break;
        }
    }

    @RequestMapping(value = "v1/bill/{id}",
                method = RequestMethod.GET,
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public Bill getBill(@PathVariable(value = "id") String billId, @RequestHeader(value = "Authorization")String auth) {
        String[] cred = Utils.decode(auth);
        return billService.getBillById(cred[0],billId);
    }

    @RequestMapping(value = "v1/bill/{id}",
            method = RequestMethod.PUT,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public Bill updateBill(@Valid @RequestBody Bill bill,
                           HttpServletResponse response, @RequestHeader(value = "Authorization")String auth,@PathVariable(value = "id") String billId) {
        String creds[] = Utils.decode(auth);
        if(bill.getId()!= null || bill.getOwnerID()!= null || bill.getCreatedTs()!= null || bill.getUpdatedTs() != null)
        {
            throw new FiledNotAllowedException("Fields not allowed");
        }

        Bill b = billService.updateBill(creds[0],billId,bill);
        if(b == null) {
            throw new PasswordNotValidException("Please enter a valid password");
        }
        response.setHeader("description","Bill created");
        return b;
    }
}
