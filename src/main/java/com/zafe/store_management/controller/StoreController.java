package com.zafe.store_management.controller;

import com.zafe.store_management.model.*;
import com.zafe.store_management.service.CashierService;
import com.zafe.store_management.service.ProductService;
import com.zafe.store_management.service.StoreService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;
    private final ProductService productService;
    private final CashierService cashierService;

    public StoreController( StoreService storeService, ProductService productService, CashierService cashierService) {
        this.storeService = storeService;
        this.productService = productService;
        this.cashierService = cashierService;
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

        storeService.createStoreWithRegisters(store, registerCount);
        return "index";
    }

    @GetMapping("/list")
    public String listStores(Model model) {
        model.addAttribute("store", storeService.findAll());
        return "store-list";
    }

    @GetMapping("/{id}/details")
    public String viewStoreDetails(@PathVariable Long id, Model model) {
        Store store = storeService.findById(id);

        List<Product> products = productService.getProductsByStoreId(id);
        List<Cashier> availableCashiers = cashierService.getAvailableCashiersForStore(id);

        model.addAttribute("store", store);
        model.addAttribute("products", products);
        model.addAttribute("availableCashiers", availableCashiers);

        return "store-details";
    }

    @GetMapping("/{id}")
    public String showStore(@PathVariable Long id, Model model) {
        Store store = storeService.findById(id);
        List<Product> products = productService.getProductsByStoreId(id);
        List<Cashier> availableCashiers = cashierService.getAvailableCashiersForStore(id);
        Map<Product, BigDecimal> productPrices = storeService.getProductsWithPrices(store);

        model.addAttribute("store", store);
        model.addAttribute("productPrices", productPrices);
        model.addAttribute("products", products);
        model.addAttribute("availableCashiers", availableCashiers);

        return "store";
    }
}
