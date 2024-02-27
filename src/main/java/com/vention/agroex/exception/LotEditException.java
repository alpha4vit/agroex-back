package com.vention.agroex.exception;

import lombok.Data;

import java.util.Map;

@Data
public class LotEditException extends RuntimeException{

    private Map<String, String> errors;

    public LotEditException() {

    }

    public LotEditException(Map<String, String> errors, String message) {
        super(message);
        this.errors = errors;
    }

    public LotEditException(String message) {
        super(message);
    }

    public LotEditException(String message, Throwable cause) {
        super(message, cause);
    }

    public LotEditException(Throwable cause) {
        super(cause);
    }

}
