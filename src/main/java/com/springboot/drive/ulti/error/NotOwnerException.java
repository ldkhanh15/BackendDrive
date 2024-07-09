package com.springboot.drive.ulti.error;

public class NotOwnerException extends RuntimeException{
    public NotOwnerException(String message) {
        super(message);
    }
}
