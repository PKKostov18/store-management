package com.zafe.store_management.service;

import com.zafe.store_management.dto.ReceiptData;
import com.zafe.store_management.dto.StoreFinancialReportDTO;
import com.zafe.store_management.exception.StoreNotFoundException;
import com.zafe.store_management.model.CashRegister;
import com.zafe.store_management.model.Product;
import com.zafe.store_management.model.Store;
import com.zafe.store_management.repository.StoreRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    public StoreFinancialReportDTO calculateFinancialReport(Store store, List<Product> products, List<ReceiptData> receipts) {
        BigDecimal totalDeliveryCost = products.stream()
                .map(p -> BigDecimal.valueOf(p.getDeliveryPrice()).multiply(BigDecimal.valueOf(p.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSalaries = store.getCashiers().stream()
                .map(c -> BigDecimal.valueOf(c.getMonthlySalary()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalIncome = receipts.stream()
                .flatMap(r -> r.getSoldProducts().stream())
                .map(s -> BigDecimal.valueOf(s.getSellingPrice()).multiply(BigDecimal.valueOf(s.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal profit = totalIncome.subtract(totalDeliveryCost).subtract(totalSalaries);

        return new StoreFinancialReportDTO(totalDeliveryCost, totalSalaries, totalIncome, profit);
    }
}
