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

    @ExceptionHandler(StoreNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleStoreNotFoundException(StoreNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleProductNotFoundException(ProductNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(CashierNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleCashierNotFoundException(CashierNotFoundException ex) {
        return ex.getMessage();
    }
}
