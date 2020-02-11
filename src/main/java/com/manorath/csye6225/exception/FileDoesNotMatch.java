package com.manorath.csye6225.exception;

public class FileDoesNotMatch extends RuntimeException {
    public FileDoesNotMatch(String message) {
        super(message);
    }
}
