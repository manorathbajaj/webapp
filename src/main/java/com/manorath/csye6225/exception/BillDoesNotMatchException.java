package com.manorath.csye6225.exception;

public class BillDoesNotMatchException extends RuntimeException {
    public BillDoesNotMatchException(String message) {
        super(message);
    }
}