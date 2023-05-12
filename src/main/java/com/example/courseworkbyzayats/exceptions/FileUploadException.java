package com.example.courseworkbyzayats.exceptions;

public class FileUploadException extends Exception{
    public FileUploadException(String message) {
        super(message);
    }

    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
