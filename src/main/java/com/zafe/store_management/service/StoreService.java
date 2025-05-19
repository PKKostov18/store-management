package com.zafe.store_management.service;

import com.zafe.store_management.exception.StoreNotFoundException;
import com.zafe.store_management.model.CashRegister;
import com.zafe.store_management.model.Product;
import com.zafe.store_management.model.Store;
import com.zafe.store_management.model.StoreSettings;
import com.zafe.store_management.repository.StoreRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class StoreService {

    private final StoreRepository storeRepository;

    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    public Store save(Store store) {
        return storeRepository.save(store);
    }

    public List<Store> findAll() {
        return storeRepository.findAll();
    }

    public Store findById(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new StoreNotFoundException(id));
    }

    public void createStoreWithRegisters(Store store, int registerCount) {
        List<CashRegister> registers = new ArrayList<>();
        for (int i = 0; i < registerCount; i++) {
            registers.add(new CashRegister());
        }
        store.setCashRegisters(registers);
        storeRepository.save(store);
    }
}
