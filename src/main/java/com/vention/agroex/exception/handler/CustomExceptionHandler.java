package com.vention.agroex.exception.handler;

import com.vention.agroex.exception.*;
import com.vention.agroex.model.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handle(Exception exception) {
        return switch (exception) {
            case JsonIOException e -> createResponse(e, HttpStatus.INTERNAL_SERVER_ERROR);
            default -> createResponse(exception, HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }

    private ResponseEntity<ExceptionResponse> createResponse(Exception e, HttpStatus status) {
        return ResponseEntity.status(status).body(new ExceptionResponse(e.getMessage(), status));
    }
}
