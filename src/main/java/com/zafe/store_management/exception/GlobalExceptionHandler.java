package com.zafe.store_management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientQuantityException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInsufficientQuantityException(InsufficientQuantityException ex) {
        return ex.getMessage();
    }
}
