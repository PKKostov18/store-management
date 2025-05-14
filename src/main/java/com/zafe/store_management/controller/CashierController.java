package com.zafe.store_management.controller;

import com.zafe.store_management.model.Cashier;
import com.zafe.store_management.model.Store;
import com.zafe.store_management.service.CashierService;
import com.zafe.store_management.service.StoreService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/store/{storeId}/cashiers")
public class CashierController {

    private final CashierService cashierService;
    private final StoreService storeService;

    public CashierController(CashierService cashierService, StoreService storeService) {
        this.cashierService = cashierService;
        this.storeService = storeService;
    }

    @GetMapping("/add")
    public String showAddCashierForm(@PathVariable Long storeId, Model model) {
        Store store = storeService.findById(storeId);
        model.addAttribute("store", store);
        model.addAttribute("cashier", new Cashier());
        return "cashier-add";
    }

    @PostMapping("/add")
    public String addCashier(@PathVariable Long storeId,
                             @ModelAttribute("cashier") @Valid Cashier cashier,
                             BindingResult result,
                             Model model) {

        if (result.hasErrors()) {
            Store store = storeService.findById(storeId);
            model.addAttribute("store", store);
            return "cashier-add";
        }

        cashierService.addCashierToStore(storeId, cashier);
        return "redirect:/store/" + storeId;
    }
}
