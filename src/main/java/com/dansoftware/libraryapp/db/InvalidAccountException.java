package com.dansoftware.libraryapp.db;

public class InvalidAccountException extends RuntimeException {

    public InvalidAccountException() {
    }

    public InvalidAccountException(String message) {
        super(message);
    }

    public InvalidAccountException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidAccountException(Throwable cause) {
        super(cause);
    }
}
