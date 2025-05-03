package com.zafe.store_management.service;

import com.zafe.store_management.model.Cashier;
import com.zafe.store_management.repository.CashierRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CashierService {

    private final CashierRepository cashierRepository;

    public CashierService(CashierRepository cashierRepository) {
        this.cashierRepository = cashierRepository;
    }

    public Cashier save(Cashier cashier) {
        return cashierRepository.save(cashier);
    }

    public List<Cashier> findAll() {
        return cashierRepository.findAll();
    }
}
