package com.vention.agroex.exception;

public class LotListSavingException extends RuntimeException {
    public LotListSavingException() {
        super();
    }

    public LotListSavingException(String message, Throwable cause) {
        super(message, cause);
    }

    public LotListSavingException(String message) {
        super(message);
    }

    public LotListSavingException(Throwable cause) {
        super(cause);
    }
}