package com.vention.agroex.exception;

import lombok.Data;

import java.util.Map;

@Data
public class InvalidArgumentException extends RuntimeException{

    private Map<String, Object> errors;

    public InvalidArgumentException() {

    }

    public InvalidArgumentException(Map<String, Object> errors, String message) {
        super(message);
        this.errors = errors;
    }

    public InvalidArgumentException(String message) {
        super(message);
    }

    public InvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidArgumentException(Throwable cause) {
        super(cause);
    }

}
