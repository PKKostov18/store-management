package com.zafe.store_management.Service;

import com.zafe.store_management.dto.CartItemDTO;
import com.zafe.store_management.dto.ReceiptData;
import com.zafe.store_management.exception.CashierNotFoundException;
import com.zafe.store_management.model.*;
import com.zafe.store_management.repository.CashierRepository;
import com.zafe.store_management.repository.ProductRepository;
import com.zafe.store_management.repository.ReceiptRepository;
import com.zafe.store_management.service.ReceiptService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReceiptServiceTest {

    @Mock
    private CashierRepository cashierRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ReceiptRepository receiptRepository;

    @InjectMocks
    private ReceiptService receiptService;


    // checkout(...)
    @Test
    void testCheckout_Success() {
        Long cashierId = 1L;
        Long productId = 10L;

        Cashier cashier = new Cashier();
        cashier.setId(cashierId);

        Product product = new Product();
        product.setId(productId);
        product.setName("Test");
        product.setQuantity(10);

        CartItemDTO item = new CartItemDTO(productId, 2, 5.0);

        when(cashierRepository.findById(cashierId)).thenReturn(Optional.of(cashier));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenReturn(product);
        when(receiptRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Receipt receipt = receiptService.checkout(cashierId, List.of(item));

        assertEquals(10.0, receipt.getTotalAmount());
        assertEquals(1, receipt.getSoldProducts().size());
        verify(productRepository).save(any());
        verify(receiptRepository).save(any());
    }

    @Test
    void testCheckout_CashierNotFound() {
        when(cashierRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(CashierNotFoundException.class, () ->
                receiptService.checkout(1L, new ArrayList<>()));
    }

    @Test
    void testCheckout_ProductNotFound() {
        Cashier cashier = new Cashier();
        cashier.setId(1L);
        when(cashierRepository.findById(1L)).thenReturn(Optional.of(cashier));
        when(productRepository.findById(any())).thenReturn(Optional.empty());

        CartItemDTO item = new CartItemDTO(1L, 1, 10.0);
        assertThrows(EntityNotFoundException.class, () ->
                receiptService.checkout(1L, List.of(item)));
    }

    // serializeReceiptToFile(...)
    @Test
    void testSerializeReceiptToFile_Success() throws IOException {
        Long receiptId = 1L;

        Store store = new Store();
        store.setName("Магазин1");

        Cashier cashier = new Cashier();
        cashier.setName("Иван");
        cashier.setStore(store);

        Product product = new Product();
        product.setName("Продукт1");

        SoldProduct sold = new SoldProduct();
        sold.setProduct(product);
        sold.setQuantity(2);
        sold.setSellingPrice(5.0);

        Receipt receipt = new Receipt();
        receipt.setId(receiptId);
        receipt.setIssuedAt(LocalDateTime.now());
        receipt.setCashier(cashier);
        receipt.setTotalAmount(10.0);
        receipt.setSoldProducts(List.of(sold));

        when(receiptRepository.findById(receiptId)).thenReturn(Optional.of(receipt));

        receiptService.serializeReceiptToFile(receiptId);

        File dir = new File("receipts/Магазин1");
        assertTrue(dir.exists());

        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
        assertNotNull(files);
        assertTrue(files.length > 0);
    }

    @Test
    void testSerializeReceiptToFile_ReceiptNotFound() {
        when(receiptRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                receiptService.serializeReceiptToFile(1L));
    }

    @Test
    void testSerializeReceiptToFile_CreatesDirectoryIfMissing() throws IOException {
        File dir = new File("receipts/Магазин1");
        if (dir.exists()) dir.delete();

        Store store = new Store();
        store.setName("Магазин1");
        Cashier cashier = new Cashier();
        cashier.setStore(store);
        cashier.setName("Иван");

        Receipt receipt = new Receipt();
        receipt.setId(2L);
        receipt.setCashier(cashier);
        receipt.setIssuedAt(LocalDateTime.now());
        receipt.setSoldProducts(new ArrayList<>());
        receipt.setTotalAmount(0.0);

        when(receiptRepository.findById(2L)).thenReturn(Optional.of(receipt));
        receiptService.serializeReceiptToFile(2L);

        assertTrue(new File("receipts/Магазин1").exists());
    }

    // loadReceiptsForStore(...)
    @Test
    void testLoadReceiptsForStore_NoDirectory() throws IOException, ClassNotFoundException {
        File dir = new File("receipts/NonExisting");
        if (dir.exists()) dir.delete();

        List<ReceiptData> receipts = receiptService.loadReceiptsForStore("NonExisting");
        assertTrue(receipts.isEmpty());
    }

    @Test
    void testLoadReceiptsForStore_Success() throws IOException, ClassNotFoundException {
        String storeName = "Магазин2";
        File dir = new File("receipts/" + storeName);
        dir.mkdirs();

        ReceiptData receiptData = new ReceiptData();
        receiptData.setReceiptId(100L);
        receiptData.setCashierName("Георги");
        receiptData.setIssuedAt(LocalDateTime.now());
        receiptData.setSoldProducts(new ArrayList<>());
        receiptData.setTotalAmount(10.0);

        File file = new File(dir, "receipt-1.txt");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(receiptData);
        }

        List<ReceiptData> result = receiptService.loadReceiptsForStore(storeName);
        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).getReceiptId());
    }

    @Test
    void testLoadReceiptsForStore_EmptyFolder() throws IOException, ClassNotFoundException {
        File dir = new File("receipts/Магазин3");
        dir.mkdirs();
        for (File f : Objects.requireNonNull(dir.listFiles())) f.delete();

        List<ReceiptData> result = receiptService.loadReceiptsForStore("Магазин3");
        assertTrue(result.isEmpty());
    }
}
