package com.manorath.csye6225.exception;

import com.manorath.csye6225.model.Bill;

public class BillDoesNotExistException extends RuntimeException {
    public BillDoesNotExistException(String message) {
        super(message);
    }
}
