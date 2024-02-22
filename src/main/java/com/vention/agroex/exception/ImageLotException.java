package com.vention.agroex.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class ImageLotException extends RuntimeException{

    private Long createdLotId;
    private Map<String, String> errors;

    public ImageLotException(String message) {
        super(message);
    }

    public ImageLotException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }

    public ImageLotException(Long createdLotId, String message) {
        super(message);
        this.createdLotId = createdLotId;
    }
}