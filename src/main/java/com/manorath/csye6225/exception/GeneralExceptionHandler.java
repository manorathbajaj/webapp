package com.manorath.csye6225.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

public class GeneralExceptionHandler {
    // Exceptions
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FiledNotAllowedException.class)
    public String handleExtraFieldExceptions() {
        return "fields not allowed to be changed";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PasswordDoesNotMatchException.class)
    public String passwordException() {return "Email and password dont match";}


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EmailAlreadyInUseException.class)
    public String emailException() {return "Email is already in use";}

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PasswordNotValidException.class)
    public String passwordNotValidException() {return "Either username or password is not valid";}

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FieldNotValidException.class)
    public String FieldNotValidException() {return "Fields are not valid";}

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UserDontMatchException.class)
    public String userNotValidException() {return "Authenticated user and provided email dont match";}

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(BillDoesNotMatchException.class)
    public String billNotAllowedException() {return "Bill id does not belong to the user";}

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(BillDoesNotExistException.class)
    public String billDoesNotExistException() {return "Bill id does not exist";}

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FileAlreadyExistsException.class)
    public String fileAlreadyExistsException() {return "File already exists";}

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(FileDoesNotExistException.class)
    public String fileDoesNotExistsException() {return "File does Not exist";}

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(FileDoesNotMatch.class)
    public String fileDoesNotMatchException() {return "File id does not match with existing file";}


}
