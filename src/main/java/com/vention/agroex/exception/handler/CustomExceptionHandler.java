package com.vention.agroex.exception.handler;

import com.vention.agroex.exception.*;
import com.vention.agroex.model.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import jakarta.persistence.EntityNotFoundException;

import java.util.HashMap;
import java.util.Map;


@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ExceptionResponse> handle(Exception exception) {
        return switch (exception) {
            case JsonIOException e -> createResponse(e, HttpStatus.INTERNAL_SERVER_ERROR, new HashMap<>());
            case EntityNotFoundException e -> createResponse(e, HttpStatus.NOT_FOUND, new HashMap<>());
            case InvalidArgumentException e -> createResponse(e, HttpStatus.BAD_REQUEST, e.getErrors());
            default -> createResponse(exception, HttpStatus.INTERNAL_SERVER_ERROR, new HashMap<>());
        };
    }

    private ResponseEntity<ExceptionResponse> createResponse(Exception e, HttpStatus status, Map<String, String> errors) {
        return ResponseEntity.status(status).body(new ExceptionResponse(errors, e.getMessage(), status));
    }
}
