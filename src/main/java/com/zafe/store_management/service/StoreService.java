package com.zafe.store_management.service;

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
    private final ProductService productService;

    public StoreService(StoreRepository storeRepository, ProductService productService) {
        this.storeRepository = storeRepository;
        this.productService = productService;
    }

    public Store save(Store store) {
        return storeRepository.save(store);
    }

    public List<Store> findAll() {
        return storeRepository.findAll();
    }

    public Store findById(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Магазинът не е намерен"));
    }

    public Map<Product, BigDecimal> getProductsWithPrices(Store store) {
        List<Product> products = productService.getProductsByStoreId(store.getId());
        StoreSettings settings = store.getStoreSettings();

        Map<Product, BigDecimal> productPrices = new LinkedHashMap<>();
        for (Product product : products) {
            BigDecimal sellingPrice = productService.calculateSellingPrice(product, settings);
            productPrices.put(product, sellingPrice);
        }

        return productPrices;
    }

    public Store createStoreWithRegisters(Store store, int registerCount) {
        List<CashRegister> registers = new ArrayList<>();
        for (int i = 0; i < registerCount; i++) {
            registers.add(new CashRegister());
        }
        store.setCashRegisters(registers);
        return storeRepository.save(store);
    }
}
