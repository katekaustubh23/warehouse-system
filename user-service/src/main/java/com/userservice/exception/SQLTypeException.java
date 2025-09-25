package com.userservice.exception;

public class SQLTypeException extends RuntimeException{

    public SQLTypeException(String message) {
        super(message);
    }

    public SQLTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
