package com.zafe.store_management.Service;

import com.zafe.store_management.exception.StoreNotFoundException;
import com.zafe.store_management.model.Cashier;
import com.zafe.store_management.model.Store;
import com.zafe.store_management.repository.CashierRepository;
import com.zafe.store_management.repository.StoreRepository;
import com.zafe.store_management.service.CashierService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CashierServiceTest {

    @Mock
    private CashierRepository cashierRepository;

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private CashierService cashierService;

    // save()
    @Test
    void save_ShouldReturnSavedCashier() {
        Cashier cashier = new Cashier();
        cashier.setName("Иван");

        when(cashierRepository.save(cashier)).thenReturn(cashier);

        Cashier saved = cashierService.save(cashier);
        assertEquals("Иван", saved.getName());
        verify(cashierRepository).save(cashier);
    }

    @Test
    void save_ShouldSaveNullCashier_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> cashierService.save(null));
    }

    @Test
    void save_ShouldHandleRepositoryException() {
        Cashier cashier = new Cashier();
        when(cashierRepository.save(any())).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> cashierService.save(cashier));
    }

    // findAll()
    @Test
    void findAll_ShouldReturnListOfCashiers() {
        List<Cashier> cashiers = List.of(new Cashier(), new Cashier());
        when(cashierRepository.findAll()).thenReturn(cashiers);

        List<Cashier> result = cashierService.findAll();

        assertEquals(2, result.size());
        verify(cashierRepository).findAll();
    }

    @Test
    void findAll_ShouldReturnEmptyList() {
        when(cashierRepository.findAll()).thenReturn(Collections.emptyList());

        List<Cashier> result = cashierService.findAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_ShouldHandleRepositoryError() {
        when(cashierRepository.findAll()).thenThrow(new RuntimeException("Error"));

        assertThrows(RuntimeException.class, () -> cashierService.findAll());
    }

    // getAvailableCashiersForStore()
    @Test
    void getAvailableCashiers_ShouldReturnAvailableCashiers() {
        List<Cashier> cashiers = List.of(new Cashier(), new Cashier());
        when(cashierRepository.findByStoreIdAndCashRegisterIsNull(1L)).thenReturn(cashiers);

        List<Cashier> result = cashierService.getAvailableCashiersForStore(1L);

        assertEquals(2, result.size());
    }

    @Test
    void getAvailableCashiers_ShouldReturnEmptyList() {
        when(cashierRepository.findByStoreIdAndCashRegisterIsNull(2L)).thenReturn(List.of());

        List<Cashier> result = cashierService.getAvailableCashiersForStore(2L);

        assertTrue(result.isEmpty());
    }

    @Test
    void getAvailableCashiers_ShouldHandleException() {
        when(cashierRepository.findByStoreIdAndCashRegisterIsNull(anyLong()))
                .thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> cashierService.getAvailableCashiersForStore(1L));
    }

    // addCashierToStore()
    @Test
    void addCashierToStore_ShouldAddCashier() {
        Store store = new Store();
        store.setCashiers(new ArrayList<>());
        Cashier cashier = new Cashier();

        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        when(storeRepository.save(any())).thenReturn(store);

        cashierService.addCashierToStore(1L, cashier);

        assertEquals(1, store.getCashiers().size());
        assertTrue(store.getCashiers().contains(cashier));
    }

    @Test
    void addCashierToStore_ShouldThrowIfStoreNotFound() {
        when(storeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(StoreNotFoundException.class,
                () -> cashierService.addCashierToStore(1L, new Cashier()));
    }

    @Test
    void addCashierToStore_ShouldHandleRepositorySaveError() {
        Store store = new Store();
        store.setCashiers(new ArrayList<>());

        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        when(storeRepository.save(any())).thenThrow(new RuntimeException("Save failed"));

        assertThrows(RuntimeException.class,
                () -> cashierService.addCashierToStore(1L, new Cashier()));
    }
}
