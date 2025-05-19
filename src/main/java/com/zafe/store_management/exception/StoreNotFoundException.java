package com.zafe.store_management.exception;

public class StoreNotFoundException extends RuntimeException {
    public StoreNotFoundException(Long storeId) {
        super("Магазин с ID " + storeId + " не е намерен.");
    }
}