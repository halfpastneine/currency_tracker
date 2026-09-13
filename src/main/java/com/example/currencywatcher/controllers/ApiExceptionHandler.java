package com.example.currencywatcher.controllers;

import com.example.currencywatcher.client.ClientException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ClientException.class)
    ProblemDetail externalApi(ClientException e) {
        HttpStatus responseStatus = switch (e.getClientErrorType()) {
            case CLIENT_ERROR -> HttpStatus.BAD_REQUEST;
            case RATE_LIMIT, API_CONFIGURATION_ERROR -> HttpStatus.SERVICE_UNAVAILABLE;
            default -> HttpStatus.BAD_GATEWAY;
        };

        ProblemDetail detail = ProblemDetail.forStatus(responseStatus);
        detail.setTitle("External API error");
        detail.setDetail(e.getMessage());
        detail.setProperty("api", e.getApiName());
        detail.setProperty("errorType", e.getClientErrorType().name());
        if (e.getHttpCode() != null) {
            detail.setProperty("externalStatus", e.getHttpCode());
        }
        return detail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail validation(MethodArgumentNotValidException e) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("Validation error");
        detail.setDetail(e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Incorrect request"));
        return detail;
    }
}
