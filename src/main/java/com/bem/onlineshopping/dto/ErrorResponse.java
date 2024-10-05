package com.bem.onlineshopping.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;


@Data
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private Map<String, String> errors;
    private String message;

    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public ErrorResponse(LocalDateTime timestamp, int status, String error,Map<String, String> errors, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.errors = errors;
        this.message = message;
    }


}