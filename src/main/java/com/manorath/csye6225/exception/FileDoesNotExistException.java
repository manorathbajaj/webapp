package com.manorath.csye6225.exception;

public class FileDoesNotExistException extends RuntimeException {

    public FileDoesNotExistException(String message) {
        super(message);
    }
}
