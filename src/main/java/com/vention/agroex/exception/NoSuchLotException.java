package com.vention.agroex.exception;

public class NoSuchLotException extends RuntimeException {
    public NoSuchLotException() {
        super();
    }

    public NoSuchLotException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchLotException(String message) {
        super(message);
    }

    public NoSuchLotException(Throwable cause) {
        super(cause);
    }
}