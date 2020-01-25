package com.manorath.csye6225.controller;

import com.manorath.csye6225.exception.FiledNotAllowedException;
import com.manorath.csye6225.exception.PasswordNotValidException;
import com.manorath.csye6225.model.Bill;
import com.manorath.csye6225.model.User;
import com.manorath.csye6225.service.BillService;
import com.manorath.csye6225.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
public class BillController {

    @Autowired
    BillService billService;

    @RequestMapping(value = "v1/bill/",
            method = RequestMethod.POST,
            consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    public Bill createUser(@Valid @RequestBody Bill bill,
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
}
