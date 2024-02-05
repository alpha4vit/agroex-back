package com.vention.agroex.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Data
@AllArgsConstructor
public class ExceptionResponse {

    Map<String, String> errors;

    private String message;

    private HttpStatus status;
}
