package com.zafe.store_management.controller;

import com.zafe.store_management.dto.ReceiptData;
import com.zafe.store_management.dto.StoreFinancialReportDTO;
import com.zafe.store_management.model.*;
import com.zafe.store_management.service.CashierService;
import com.zafe.store_management.service.ProductService;
import com.zafe.store_management.service.ReceiptService;
import com.zafe.store_management.service.StoreService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;
    private final ProductService productService;
    private final CashierService cashierService;
    private final ReceiptService receiptService;

    public StoreController(StoreService storeService, ProductService productService, CashierService cashierService, ReceiptService receiptService) {
        this.storeService = storeService;
        this.productService = productService;
        this.cashierService = cashierService;
        this.receiptService = receiptService;
    }

    @GetMapping("/add")
    public String showCreateStoreForm(Model model) {
        Store store = new Store();
        store.setStoreSettings(new StoreSettings());
        model.addAttribute("store", store);
        return "store-add";
    }

    @PostMapping("/add")
    public String createStore(@Valid @ModelAttribute Store store, BindingResult bindingResult, @RequestParam("registerCount") int registerCount) {
        if (bindingResult.hasErrors()) {
            return "store-add";
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
    public String viewStoreDetails(@PathVariable Long id, Model model) throws IOException, ClassNotFoundException {
        Store store = storeService.findById(id);

        List<Product> products = productService.getProductsByStoreId(id);
        List<Cashier> availableCashiers = cashierService.getAvailableCashiersForStore(id);
        List<ReceiptData> receiptDataList = receiptService.loadReceiptsForStore(store.getName());
        StoreFinancialReportDTO report = storeService.calculateFinancialReport(store, products, receiptDataList);

        model.addAttribute("store", store);
        model.addAttribute("products", products);
        model.addAttribute("availableCashiers", availableCashiers);
        model.addAttribute("receipts", receiptDataList);
        model.addAttribute("totalDeliveryCost", report.getTotalDeliveryCost());
        model.addAttribute("totalSalaries", report.getTotalSalaries());
        model.addAttribute("totalIncome", report.getTotalIncome());
        model.addAttribute("profit", report.getProfit());
        model.addAttribute("financialReport", report);

        return "store-details";
    }

    @GetMapping("/{id}")
    public String showStore(@PathVariable Long id, Model model) {
        Store store = storeService.findById(id);
        List<Product> products = productService.getProductsByStoreId(id);
        List<Cashier> availableCashiers = cashierService.getAvailableCashiersForStore(id);
        Map<Product, BigDecimal> productPrices = productService.getProductsWithPrices(store);

        model.addAttribute("store", store);
        model.addAttribute("productPrices", productPrices);
        model.addAttribute("products", products);
        model.addAttribute("availableCashiers", availableCashiers);

        return "store";
    }
}
