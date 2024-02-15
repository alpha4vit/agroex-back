package com.vention.agroex.exception;

import lombok.Getter;

@Getter
public class ImageLotException extends RuntimeException{

    private Long createdLotId;

    public ImageLotException(String message) {
        super(message);
    }

    public ImageLotException(Long createdLotId, String message) {
        super(message);
        this.createdLotId = createdLotId;
    }
}