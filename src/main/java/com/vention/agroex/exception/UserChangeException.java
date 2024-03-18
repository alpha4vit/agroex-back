package com.vention.agroex.exception;

public class UserChangeException extends RuntimeException{

    public UserChangeException() {
    }

    public UserChangeException(String message) {
        super(message);
    }

    public UserChangeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserChangeException(Throwable cause) {
        super(cause);
    }
}