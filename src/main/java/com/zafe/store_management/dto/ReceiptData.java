package com.zafe.store_management.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class ReceiptData implements Serializable {
    private Long receiptId;
    private String cashierName;
    private LocalDateTime issuedAt;
    private List<SoldProductDataDTO> soldProducts;
    private double totalAmount;

    public Long getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(Long receiptId) {
        this.receiptId = receiptId;
    }

    public String getCashierName() {
        return cashierName;
    }

    public void setCashierName(String cashierName) {
        this.cashierName = cashierName;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public List<SoldProductDataDTO> getSoldProducts() {
        return soldProducts;
    }

    public void setSoldProducts(List<SoldProductDataDTO> soldProducts) {
        this.soldProducts = soldProducts;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
