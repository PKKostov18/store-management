package com.zafe.store_management.model;

import jakarta.persistence.*;

@Entity
public class StoreSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double foodMarkupPercentage;

    private double nonFoodMarkupPercentage;

    private int daysBeforeExpirationForDiscount;

    private double discountPercentage;

    public StoreSettings() {};

    public StoreSettings(Long id, double foodMarkupPercentage, double nonFoodMarkupPercentage, int daysBeforeExpirationForDiscount, double discountPercentage) {
        this.id = id;
        this.foodMarkupPercentage = foodMarkupPercentage;
        this.nonFoodMarkupPercentage = nonFoodMarkupPercentage;
        this.daysBeforeExpirationForDiscount = daysBeforeExpirationForDiscount;
        this.discountPercentage = discountPercentage;
    }

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