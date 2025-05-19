package com.zafe.store_management.exception;

public class CashierNotFoundException extends RuntimeException {
    public CashierNotFoundException(Long cashierId) {
        super("Касиер с ID " + cashierId + " не е намерен.");
    }
}