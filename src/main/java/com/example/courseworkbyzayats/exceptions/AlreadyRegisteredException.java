package com.example.courseworkbyzayats.exceptions;

public class AlreadyRegisteredException extends Exception{
    public AlreadyRegisteredException(String message) {
        super(message);
    }

    public AlreadyRegisteredException(String message, Throwable cause) {
        super(message, cause);
    }
}
