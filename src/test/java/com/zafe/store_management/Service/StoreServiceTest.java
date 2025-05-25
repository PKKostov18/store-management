package com.zafe.store_management.Service;

import com.zafe.store_management.dto.ReceiptData;
import com.zafe.store_management.dto.SoldProductDataDTO;
import com.zafe.store_management.dto.StoreFinancialReportDTO;
import com.zafe.store_management.exception.StoreNotFoundException;
import com.zafe.store_management.model.CashRegister;
import com.zafe.store_management.model.Cashier;
import com.zafe.store_management.model.Product;
import com.zafe.store_management.model.Store;
import com.zafe.store_management.repository.StoreRepository;
import com.zafe.store_management.service.StoreService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private StoreService storeService;

    // save(...)
    @Test
    void testSave_ValidStore() {
        Store store = new Store();
        store.setName("Магазин4");

        when(storeRepository.save(store)).thenReturn(store);

        Store saved = storeService.save(store);
        assertEquals("Магазин4", saved.getName());
        verify(storeRepository).save(store);
    }

    @Test
    void testSave_NullStore() {
        assertThrows(IllegalArgumentException.class, () -> storeService.save(null));
    }

    @Test
    void testSave_StoreWithRegisters() {
        Store store = new Store();
        store.setName("Магазин5");
        store.setCashRegisters(List.of(new CashRegister()));

        when(storeRepository.save(store)).thenReturn(store);

        Store saved = storeService.save(store);
        assertEquals(1, saved.getCashRegisters().size());
    }

    // findAll()
    @Test
    void testFindAll_ReturnsMultipleStores() {
        when(storeRepository.findAll()).thenReturn(List.of(new Store(), new Store()));
        List<Store> result = storeService.findAll();
        assertEquals(2, result.size());
    }

    @Test
    void testFindAll_EmptyList() {
        when(storeRepository.findAll()).thenReturn(Collections.emptyList());
        List<Store> result = storeService.findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindAll_VerifyRepositoryCall() {
        storeService.findAll();
        verify(storeRepository).findAll();
    }

    // findById(...)
    @Test
    void testFindById_Found() {
        Store store = new Store();
        store.setId(1L);
        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));

        Store result = storeService.findById(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void testFindById_NotFound() {
        when(storeRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(StoreNotFoundException.class, () -> storeService.findById(99L));
    }

    @Test
    void testFindById_VerifyCall() {
        Store mockStore = new Store();
        when(storeRepository.findById(5L)).thenReturn(Optional.of(mockStore));

        storeService.findById(5L);

        verify(storeRepository).findById(5L);
    }

    // createStoreWithRegisters(...)
    @Test
    void testCreateStoreWithRegisters_CreatesCorrectCount() {
        Store store = new Store();
        storeService.createStoreWithRegisters(store, 3);
        assertEquals(3, store.getCashRegisters().size());
    }

    @Test
    void testCreateStoreWithRegisters_SavesStore() {
        Store store = new Store();
        storeService.createStoreWithRegisters(store, 2);
        verify(storeRepository).save(store);
    }

    @Test
    void testCreateStoreWithRegisters_ZeroRegisters() {
        Store store = new Store();
        storeService.createStoreWithRegisters(store, 0);
        assertNotNull(store.getCashRegisters());
        assertEquals(0, store.getCashRegisters().size());
    }

    // calculateFinancialReport(...)
    @Test
    void testCalculateFinancialReport_CorrectTotals() {
        Store store = new Store();
        Cashier c1 = new Cashier(); c1.setMonthlySalary(1000);
        Cashier c2 = new Cashier(); c2.setMonthlySalary(1500);
        store.setCashiers(List.of(c1, c2));

        Product p1 = new Product(); p1.setQuantity(5); p1.setDeliveryPrice(3.0);
        Product p2 = new Product(); p2.setQuantity(2); p2.setDeliveryPrice(10.0);

        SoldProductDataDTO sold1 = new SoldProductDataDTO("Продукт 1", 2, 20.0);
        SoldProductDataDTO sold2 = new SoldProductDataDTO("Продукт 2", 1, 30.0);

        ReceiptData r1 = new ReceiptData(); r1.setSoldProducts(List.of(sold1));
        ReceiptData r2 = new ReceiptData(); r2.setSoldProducts(List.of(sold2));

        StoreFinancialReportDTO report = storeService.calculateFinancialReport(store, List.of(p1, p2), List.of(r1, r2));

        assertEquals(BigDecimal.valueOf(35.0), report.getTotalDeliveryCost()); // 5*3 + 2*10
        assertEquals(BigDecimal.valueOf(2500.0), report.getTotalSalaries()); // 1000 + 1500
        assertEquals(BigDecimal.valueOf(70.0), report.getTotalIncome()); // 2*20 + 1*30
        assertEquals(BigDecimal.valueOf(-2465.0), report.getProfit()); // 70 - 35 - 2500
    }

    @Test
    void testCalculateFinancialReport_EmptyInputs() {
        Store store = new Store();
        store.setCashiers(Collections.emptyList());
        List<Product> products = Collections.emptyList();
        List<ReceiptData> receipts = Collections.emptyList();

        StoreFinancialReportDTO report = storeService.calculateFinancialReport(store, products, receipts);
        assertEquals(BigDecimal.ZERO, report.getTotalDeliveryCost());
        assertEquals(BigDecimal.ZERO, report.getTotalSalaries());
        assertEquals(BigDecimal.ZERO, report.getTotalIncome());
        assertEquals(BigDecimal.ZERO, report.getProfit());
    }

    @Test
    void testCalculateFinancialReport_NullQuantities() {
        Store store = new Store();
        store.setCashiers(List.of(new Cashier() {{
            setMonthlySalary(0);
        }}));

        Product p = new Product();
        p.setDeliveryPrice(0);
        p.setQuantity(0);

        SoldProductDataDTO sold = new SoldProductDataDTO("Продукт1", 0, 0.0);
        ReceiptData r = new ReceiptData();
        r.setSoldProducts(List.of(sold));

        StoreFinancialReportDTO report = storeService.calculateFinancialReport(store, List.of(p), List.of(r));
        assertEquals(BigDecimal.ZERO.setScale(1), report.getTotalIncome());
        assertEquals(BigDecimal.ZERO.setScale(1), report.getProfit());
    }
}
