package com.zafe.store_management.controller;

import com.zafe.store_management.dto.CartItemDTO;
import com.zafe.store_management.dto.ReceiptDTO;
import com.zafe.store_management.model.Receipt;
import com.zafe.store_management.service.ReceiptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/receipt")
public class ReceiptController {

    private final ReceiptService receiptService;

    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestParam("cashierId") Long cashierId,
                                      @RequestBody List<CartItemDTO> cartItems) {
        try {
            Receipt receipt = receiptService.checkout(cashierId, cartItems);

            receiptService.serializeReceiptToFile(receipt.getId());

            ReceiptDTO dto = new ReceiptDTO();
            dto.setTotalAmount(receipt.getTotalAmount());
            dto.setCashierName(receipt.getCashier().getName());
            dto.setProductNames(
                    receipt.getSoldProducts().stream()
                            .map(sp -> sp.getProduct().getName())
                            .toList()
            );

            return ResponseEntity.ok(dto);

        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
