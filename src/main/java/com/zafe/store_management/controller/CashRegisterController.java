package com.zafe.store_management.controller;

import com.zafe.store_management.model.CashRegister;
import com.zafe.store_management.model.Cashier;
import com.zafe.store_management.repository.CashRegisterRepository;
import com.zafe.store_management.repository.CashierRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/cashRegister")
public class CashRegisterController {

    private final CashRegisterRepository registerRepo;
    private final CashierRepository cashierRepo;

    public CashRegisterController(CashRegisterRepository registerRepo, CashierRepository cashierRepo) {
        this.registerRepo = registerRepo;
        this.cashierRepo = cashierRepo;
    }

    @PostMapping("/{id}/assign")
    public String assignCashier(@PathVariable Long id, @RequestParam Long cashierId) {
        CashRegister register = registerRepo.findById(id).orElseThrow();
        Cashier cashier = cashierRepo.findById(cashierId).orElseThrow();

        register.setCashier(cashier);
        registerRepo.save(register);

        return "redirect:/store/" + register.getStore().getId();
    }

    @PostMapping("/{id}/unassign")
    public String unassignCashier(@PathVariable Long id) {
        CashRegister register = registerRepo.findById(id).orElseThrow();
        register.setCashier(null);
        registerRepo.save(register);

        return "redirect:/store/" + register.getStore().getId();
    }
}
