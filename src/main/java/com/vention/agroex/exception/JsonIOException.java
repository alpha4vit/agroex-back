package com.vention.agroex.exception;

public class JsonIOException extends RuntimeException{

    public JsonIOException() {
    }

    public JsonIOException(String message) {
        super(message);
    }

    public JsonIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonIOException(Throwable cause) {
        super(cause);
    }
}
