package com.vention.agroex.exception;

public class LotListInitializationException extends RuntimeException {
    public LotListInitializationException() {
        super();
    }

    public LotListInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public LotListInitializationException(String message) {
        super(message);
    }

    public LotListInitializationException(Throwable cause) {
        super(cause);
    }
}