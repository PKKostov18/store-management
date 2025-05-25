package com.zafe.store_management.dto;

public class CartItemDTO {
    private Long productId;
    private int quantity;
    private double sellingPrice;

    public CartItemDTO() {};

    public CartItemDTO(Long productId, int quantity, double sellingPrice) {
        this.productId = productId;
        this.quantity = quantity;
        this.sellingPrice = sellingPrice;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
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