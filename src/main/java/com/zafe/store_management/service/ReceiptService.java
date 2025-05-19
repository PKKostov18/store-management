package com.zafe.store_management.service;

import com.zafe.store_management.dto.CartItemDTO;
import com.zafe.store_management.exception.CashierNotFoundException;
import com.zafe.store_management.model.Cashier;
import com.zafe.store_management.model.Product;
import com.zafe.store_management.model.Receipt;
import com.zafe.store_management.model.SoldProduct;
import com.zafe.store_management.repository.CashierRepository;
import com.zafe.store_management.repository.ProductRepository;
import com.zafe.store_management.repository.ReceiptRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReceiptService {

    private final ProductRepository productRepository;
    private final ReceiptRepository receiptRepository;
    private final CashierRepository cashierRepository;

    public ReceiptService(ProductRepository productRepository,
                          ReceiptRepository receiptRepository,
                          CashierRepository cashierRepository) {
        this.productRepository = productRepository;
        this.receiptRepository = receiptRepository;
        this.cashierRepository = cashierRepository;
    }

    public Receipt checkout(Long cashierId, List<CartItemDTO> cartItems) {
        Cashier cashier = cashierRepository.findById(cashierId)
                .orElseThrow(() -> new CashierNotFoundException(cashierId));

        Receipt receipt = new Receipt();
        receipt.setCashier(cashier);
        receipt.setIssuedAt(LocalDateTime.now());

        double totalAmount = 0.0;
        List<SoldProduct> soldProducts = new ArrayList<>();

        for (CartItemDTO item : cartItems) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Продуктът не е намерен"));

            // Намаляване на наличното количество
            product.setQuantity(product.getQuantity() - item.getQuantity());
            productRepository.save(product);

            // Изчисляване на цена
            double sellingPrice = item.getSellingPrice();

            SoldProduct soldProduct = new SoldProduct();
            soldProduct.setProduct(product);
            soldProduct.setQuantity(item.getQuantity());
            soldProduct.setSellingPrice(sellingPrice);
            soldProduct.setReceipt(receipt);

            soldProducts.add(soldProduct);

            totalAmount += sellingPrice * item.getQuantity();
        }

        receipt.setSoldProducts(soldProducts);
        receipt.setTotalAmount(totalAmount);

        System.out.println("=== Checkout Start ===");
        System.out.println("Касиер ID: " + cashierId);
        System.out.println("Продукти:");
        for (CartItemDTO item : cartItems) {
            System.out.printf("ID: %d, Qty: %d, Цена: %.2f%n", item.getProductId(), item.getQuantity(), item.getSellingPrice());
        }
        System.out.println("=== Край ===");

        return receiptRepository.save(receipt);
    }
}
