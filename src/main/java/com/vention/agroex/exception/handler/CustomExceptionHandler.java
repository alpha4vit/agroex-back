package com.vention.agroex.exception.handler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.vention.agroex.exception.ImageException;
import com.vention.agroex.exception.ImageLotException;
import com.vention.agroex.exception.InvalidArgumentException;
import com.vention.agroex.exception.JsonIOException;
import com.vention.agroex.exception.InvalidBetException;
import com.vention.agroex.model.ExceptionResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        record ErrorField(String field, String message) {}
        return switch (ex.getCause()) {
            case InvalidFormatException e -> {
                Map<String, String> map = e.getPath().stream()
                        .map(field -> new ErrorField(
                                field.getFieldName(), String.format("Ivalid input type: %s", e.getValue()))
                        )
                        .collect(toMap(ErrorField::field, ErrorField::message));
                yield createResponse(ex, HttpStatus.BAD_REQUEST, map);
            }
            default -> createResponse(ex, HttpStatus.BAD_REQUEST, new HashMap<>());
        };
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.info(ex.getMessage());
        return createResponse(ex, HttpStatus.BAD_REQUEST, new HashMap<>());
    }

    @ExceptionHandler
    public ResponseEntity<Object> handle(Exception exception) {
        logger.error(exception.getMessage());
        return switch (exception) {
            case JsonIOException e -> createResponse(e, HttpStatus.INTERNAL_SERVER_ERROR, new HashMap<>());
            case EntityNotFoundException e -> createResponse(e, HttpStatus.NOT_FOUND, new HashMap<>());
            case InvalidArgumentException e -> createResponse(e, HttpStatus.BAD_REQUEST, e.getErrors());
            case InvalidBetException e -> createResponse(e, HttpStatus.BAD_REQUEST, new HashMap<>());
            case ImageLotException e -> createResponse(e, HttpStatus.BAD_REQUEST, e.getErrors());
            case ImageException e -> createResponse(e, HttpStatus.BAD_REQUEST, new HashMap<>());
            default -> createResponse(exception, HttpStatus.INTERNAL_SERVER_ERROR, new HashMap<>());
        };
    }

    private ResponseEntity<Object> createResponse(Exception e, HttpStatus status, Map<String, String> errors) {
        return ResponseEntity.status(status).body(new ExceptionResponse(errors, e.getMessage(), status));
    }
}
