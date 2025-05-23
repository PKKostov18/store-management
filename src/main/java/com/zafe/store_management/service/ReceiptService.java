package com.zafe.store_management.service;

import com.zafe.store_management.dto.CartItemDTO;
import com.zafe.store_management.dto.ReceiptData;
import com.zafe.store_management.dto.SoldProductDataDTO;
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

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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

    public void serializeReceiptToFile(Long receiptId) throws IOException {
        Receipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new EntityNotFoundException("Receipt not found"));

        ReceiptData data = new ReceiptData();
        data.setReceiptId(receipt.getId());
        data.setIssuedAt(receipt.getIssuedAt());
        data.setCashierName(receipt.getCashier().getName());
        data.setTotalAmount(receipt.getTotalAmount());

        List<SoldProductDataDTO> soldProducts = receipt.getSoldProducts().stream().map(sp -> {
            SoldProductDataDTO dto = new SoldProductDataDTO();
            dto.setProductName(sp.getProduct().getName());
            dto.setQuantity(sp.getQuantity());
            dto.setSellingPrice(sp.getSellingPrice());
            return dto;
        }).toList();

        data.setSoldProducts(soldProducts);

        // Папка на магазина
        String storeName = receipt.getCashier().getStore().getName();
        File storeDir = new File("receipts/" + storeName);
        if (!storeDir.exists()) storeDir.mkdirs();

        int nextNumber = 1;
        String[] existingFiles = storeDir.list((dir, name) -> name.matches("receipt-(\\d+)\\.txt"));
        if (existingFiles != null) {
            nextNumber += existingFiles.length;
        }

        File file = new File(storeDir, "receipt-" + nextNumber + ".txt");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(data);
        }
    }

    public List<ReceiptData> loadReceiptsForStore(String storeName) throws IOException, ClassNotFoundException {
        File storeDir = new File("receipts/" + storeName);
        List<ReceiptData> receipts = new ArrayList<>();

        if (!storeDir.exists()) return receipts;

        for (File file : Objects.requireNonNull(storeDir.listFiles())) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                ReceiptData receipt = (ReceiptData) ois.readObject();
                receipts.add(receipt);
            }
        }

        receipts.sort(Comparator.comparingLong(ReceiptData::getReceiptId));
        return receipts;
    }
}
