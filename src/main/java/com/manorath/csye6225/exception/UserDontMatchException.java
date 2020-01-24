package com.manorath.csye6225.exception;

public class UserDontMatchException extends RuntimeException {
    public UserDontMatchException(String message) {
        super(message);
    }
}
