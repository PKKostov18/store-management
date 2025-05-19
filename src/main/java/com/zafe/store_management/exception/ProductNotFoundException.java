package com.zafe.store_management.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String productName, Long storeId) {
        super("Продукт \"" + productName + "\" не е намерен в магазин с ID " + storeId + ".");
    }
}