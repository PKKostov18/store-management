package com.zafe.store_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private double deliveryPrice;

    private int quantity;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    @Column(nullable = false)
    private boolean hasExpirationDate;

    private LocalDate expirationDate;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @Column(nullable = false, columnDefinition = "bit default 0")
    private boolean deleted;

    public Product() {};

    public Product(Long id, String name, double deliveryPrice, int quantity, ProductCategory category, boolean hasExpirationDate, LocalDate expirationDate, Store store, boolean deleted) {
        this.id = id;
        this.name = name;
        this.deliveryPrice = deliveryPrice;
        this.quantity = quantity;
        this.category = category;
        this.hasExpirationDate = hasExpirationDate;
        this.expirationDate = expirationDate;
        this.store = store;
        this.deleted = deleted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(double deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public boolean isHasExpirationDate() {
        return hasExpirationDate;
    }

    public void setHasExpirationDate(boolean hasExpirationDate) {
        this.hasExpirationDate = hasExpirationDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
