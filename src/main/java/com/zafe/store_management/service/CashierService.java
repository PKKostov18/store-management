package com.zafe.store_management.service;

import com.zafe.store_management.exception.StoreNotFoundException;
import com.zafe.store_management.model.Cashier;
import com.zafe.store_management.model.Store;
import com.zafe.store_management.repository.CashierRepository;
import com.zafe.store_management.repository.StoreRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CashierService {

    private final CashierRepository cashierRepository;
    private final StoreRepository storeRepository;

    public CashierService(CashierRepository cashierRepository, StoreRepository storeRepository) {
        this.cashierRepository = cashierRepository;
        this.storeRepository = storeRepository;
    }

    public Cashier save(Cashier cashier) {
        return cashierRepository.save(cashier);
    }

    public List<Cashier> findAll() {
        return cashierRepository.findAll();
    }

    public List<Cashier> getAvailableCashiersForStore(Long storeId) {
        return cashierRepository.findByStoreIdAndCashRegisterIsNull(storeId);
    }

    public void addCashierToStore(Long storeId, Cashier cashier) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreNotFoundException(storeId));
        store.getCashiers().add(cashier);
        storeRepository.save(store);
    }
}
