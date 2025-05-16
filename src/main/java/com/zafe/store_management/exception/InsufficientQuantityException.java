package com.zafe.store_management.exception;

public class InsufficientQuantityException extends RuntimeException {
    public InsufficientQuantityException(String productName, int requested, int available) {
        super("Недостатъчно количество от продукт \"" + productName + "\". Заявено: " + requested + ", налично: " + available);
    }
}