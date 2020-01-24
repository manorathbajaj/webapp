package com.manorath.csye6225.exception;

public class FieldNotValidException extends RuntimeException {
    public FieldNotValidException (String message) {
        super("Field is not allowed");
    }
}
