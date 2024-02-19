package com.vention.agroex.exception;

public class InvalidBetException extends RuntimeException{

    public InvalidBetException() {
    }

    public InvalidBetException(String message) {
        super(message);
    }

    public InvalidBetException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidBetException(Throwable cause) {
        super(cause);
    }
}