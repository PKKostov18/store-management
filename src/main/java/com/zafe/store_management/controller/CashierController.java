package com.zafe.store_management.controller;

import com.zafe.store_management.model.Cashier;
import com.zafe.store_management.model.Store;
import com.zafe.store_management.repository.StoreRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/store/{storeId}/cashiers")
public class CashierController {

    private final StoreRepository storeRepository;

    public CashierController(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @GetMapping("/add")
    public String showAddCashierForm(@PathVariable Long storeId, Model model) {
        Store store = storeRepository.findById(storeId).orElseThrow();
        model.addAttribute("store", store);
        model.addAttribute("cashier", new Cashier());
        return "cashier-add";
    }

    @PostMapping("/add")
    public String addCashier(@PathVariable Long storeId,
                             @ModelAttribute("cashier") @Valid Cashier cashier,
                             BindingResult result,
                             Model model) {
        Store store = storeRepository.findById(storeId).orElseThrow();

        if (result.hasErrors()) {
            model.addAttribute("store", store);
            return "cashier-add";
        }

        store.getCashiers().add(cashier);
        storeRepository.save(store);
        return "redirect:/store/" + storeId;
    }
}
