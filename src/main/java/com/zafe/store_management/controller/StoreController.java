package com.zafe.store_management.controller;

import com.zafe.store_management.model.*;
import com.zafe.store_management.repository.CashierRepository;
import com.zafe.store_management.repository.ProductRepository;
import com.zafe.store_management.repository.StoreRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/store")
public class StoreController {

    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final CashierRepository cashierRepository;

    public StoreController(StoreRepository storeRepository, ProductRepository productRepository, CashierRepository cashierRepository) {
        this.storeRepository = storeRepository;
        this.productRepository = productRepository;
        this.cashierRepository = cashierRepository;
    }

    @GetMapping("/add")
    public String showCreateStoreForm(Model model) {
        Store store = new Store();
        store.setStoreSettings(new StoreSettings());
        model.addAttribute("store", store);
        return "store-create";
    }

    @PostMapping("/add")
    public String createStore(@Valid @ModelAttribute Store store, BindingResult bindingResult, @RequestParam("registerCount") int registerCount) {
        if (bindingResult.hasErrors()) {
            return "store-create";
        }
        List<CashRegister> registers = new ArrayList<>();
        for (int i = 0; i < registerCount; i++) {
            registers.add(new CashRegister());
        }

        store.setCashRegisters(registers);
        storeRepository.save(store);
        return "index";
    }

    @GetMapping("/list")
    public String listStores(Model model) {
        model.addAttribute("store", storeRepository.findAll());
        return "store-list";
    }

    @GetMapping("/{id}")
    public String viewStoreDetails(@PathVariable Long id, Model model) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Магазинът не е намерен"));

        List<Product> products = productRepository.findByStoreId(id);
        List<Cashier> availableCashiers = cashierRepository.findByStoreIdAndCashRegisterIsNull(id); // <--- ключово

        model.addAttribute("store", store);
        model.addAttribute("products", products);
        model.addAttribute("availableCashiers", availableCashiers); // <--- подаваме към Thymeleaf

        return "store-details";
    }
}
