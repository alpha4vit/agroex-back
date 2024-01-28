package com.vention.agroex.exception;


public class UserListInitializationException extends RuntimeException {

    public UserListInitializationException() {
    }

    public UserListInitializationException(String message) {
        super(message);
    }

    public UserListInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserListInitializationException(Throwable cause) {
        super(cause);
    }
}
