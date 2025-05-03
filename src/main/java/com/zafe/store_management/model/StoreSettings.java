package com.zafe.store_management.model;

import jakarta.persistence.*;

@Entity
public class StoreSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double foodMarkupPercentage;

    private double nonFoodMarkupPercentage;

    private double totalTurnover;

    private int issuedReceipts;

    private int daysBeforeExpirationForDiscount;

    private double discountPercentage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getFoodMarkupPercentage() {
        return foodMarkupPercentage;
    }

    public void setFoodMarkupPercentage(double foodMarkupPercentage) {
        this.foodMarkupPercentage = foodMarkupPercentage;
    }

    public double getNonFoodMarkupPercentage() {
        return nonFoodMarkupPercentage;
    }

    public void setNonFoodMarkupPercentage(double nonFoodMarkupPercentage) {
        this.nonFoodMarkupPercentage = nonFoodMarkupPercentage;
    }

    public double getTotalTurnover() {
        return totalTurnover;
    }

    public void setTotalTurnover(double totalTurnover) {
        this.totalTurnover = totalTurnover;
    }

    public int getIssuedReceipts() {
        return issuedReceipts;
    }

    public void setIssuedReceipts(int issuedReceipts) {
        this.issuedReceipts = issuedReceipts;
    }

    public int getDaysBeforeExpirationForDiscount() {
        return daysBeforeExpirationForDiscount;
    }

    public void setDaysBeforeExpirationForDiscount(int daysBeforeExpirationForDiscount) {
        this.daysBeforeExpirationForDiscount = daysBeforeExpirationForDiscount;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }
}