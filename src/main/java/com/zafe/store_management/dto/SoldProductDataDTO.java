package com.zafe.store_management.dto;

import java.io.Serial;
import java.io.Serializable;

public class SoldProductDataDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String productName;
    private int quantity;
    private double sellingPrice;

    public SoldProductDataDTO() {};

    public SoldProductDataDTO(String productName, int quantity, double sellingPrice) {
        this.productName = productName;
        this.quantity = quantity;
        this.sellingPrice = sellingPrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }
}