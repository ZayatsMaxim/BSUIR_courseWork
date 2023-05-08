package com.example.courseworkbyzayats.exceptions;

public class AlreadyInUseException extends Exception{
    public AlreadyInUseException(String message) {
        super(message);
    }

    public AlreadyInUseException(String message, Throwable cause) {
        super(message, cause);
    }
}
