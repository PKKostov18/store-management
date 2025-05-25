package com.zafe.store_management.model;

import jakarta.persistence.*;

@Entity
public class CashRegister {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cashier_id")
    private Cashier cashier;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    public CashRegister() {}

    public CashRegister(Long id, Cashier cashier, Store store) {
        this.id = id;
        this.cashier = cashier;
        this.store = store;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cashier getCashier() {
        return cashier;
    }

    public void setCashier(Cashier cashier) {
        this.cashier = cashier;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }
}
