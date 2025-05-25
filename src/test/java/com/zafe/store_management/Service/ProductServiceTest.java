package com.zafe.store_management.Service;

import com.zafe.store_management.model.Product;
import com.zafe.store_management.model.ProductCategory;
import com.zafe.store_management.model.Store;
import com.zafe.store_management.model.StoreSettings;
import com.zafe.store_management.repository.ProductRepository;
import com.zafe.store_management.repository.StoreRepository;
import com.zafe.store_management.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock private StoreRepository storeRepository;
    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productService = new ProductService(productRepository, storeRepository);
    }

    // save()
    @Test
    void save_ShouldCallRepositorySave() {
        Product product = new Product();
        productService.save(product);
        verify(productRepository).save(product);
    }

    @Test
    void save_ShouldNotThrowException() {
        Product product = new Product();
        assertDoesNotThrow(() -> productService.save(product));
    }

    @Test
    void save_ShouldSaveCorrectProduct() {
        Product product = new Product();
        product.setName("Кока Кола");
        productService.save(product);
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertEquals("Кока Кола", captor.getValue().getName());
    }

    // findAll()
    @Test
    void findAll_ShouldReturnProductList() {
        List<Product> expected = List.of(new Product(), new Product());
        when(productRepository.findAll()).thenReturn(expected);
        List<Product> result = productService.findAll();
        assertEquals(2, result.size());
    }

    @Test
    void findAll_ShouldReturnEmptyList() {
        when(productRepository.findAll()).thenReturn(Collections.emptyList());
        List<Product> result = productService.findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_ShouldCallRepositoryMethod() {
        productService.findAll();
        verify(productRepository).findAll();
    }

    // calculateSellingPrice(...)
    @Test
    void calculateSellingPrice_ShouldApplyFoodMarkup() {
        Product p = new Product(null, "Apple", 100.0, 0, ProductCategory.FOOD, false, null, null, false);
        StoreSettings s = new StoreSettings(null, 10, 0, 0, 0);
        BigDecimal price = productService.calculateSellingPrice(p, s);
        assertEquals(BigDecimal.valueOf(110.00).setScale(2), price);
    }

    @Test
    void calculateSellingPrice_ShouldApplyDiscountForExpiringProduct() {
        Product p = new Product(null, "Milk", 100.0, 0, ProductCategory.FOOD, true, LocalDate.now().plusDays(2), null, false);
        StoreSettings s = new StoreSettings(null, 10, 0, 3, 20);
        BigDecimal price = productService.calculateSellingPrice(p, s);
        assertEquals(BigDecimal.valueOf(88.00).setScale(2), price);
    }

    @Test
    void calculateSellingPrice_ShouldIgnoreDiscountIfNotExpiringSoon() {
        Product p = new Product(null, "Toothpaste", 100.0, 0, ProductCategory.NON_FOOD, true, LocalDate.now().plusDays(10), null, false);
        StoreSettings s = new StoreSettings(null, 0, 20, 5, 50);
        BigDecimal price = productService.calculateSellingPrice(p, s);
        assertEquals(BigDecimal.valueOf(120.00).setScale(2), price);
    }

    // getProductsByStoreId()
    @Test
    void getProductsByStoreId_ShouldCallRepository() {
        productService.getProductsByStoreId(1L);
        verify(productRepository).findByStoreIdAndDeletedFalse(1L);
    }

    @Test
    void getProductsByStoreId_ShouldReturnEmptyList() {
        when(productRepository.findByStoreIdAndDeletedFalse(anyLong())).thenReturn(Collections.emptyList());
        List<Product> result = productService.getProductsByStoreId(1L);
        assertTrue(result.isEmpty());
    }

    @Test
    void getProductsByStoreId_ShouldReturnCorrectProducts() {
        Product p = new Product();
        when(productRepository.findByStoreIdAndDeletedFalse(1L)).thenReturn(List.of(p));
        List<Product> result = productService.getProductsByStoreId(1L);
        assertEquals(1, result.size());
    }

    // saveProductForStore()
    @Test
    void saveProductForStore_ShouldSetStoreAndSave() {
        Store store = new Store();
        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        Product product = new Product();
        product.setHasExpirationDate(false);
        productService.saveProductForStore(product, 1L);
        assertEquals(store, product.getStore());
        verify(productRepository).save(product);
    }

    @Test
    void saveProductForStore_ShouldThrowIfStoreNotFound() {
        when(storeRepository.findById(any())).thenReturn(Optional.empty());
        Product p = new Product();
        assertThrows(RuntimeException.class, () -> productService.saveProductForStore(p, 99L));
    }

    @Test
    void saveProductForStore_ShouldClearExpirationDateIfNotRequired() {
        Store store = new Store();
        when(storeRepository.findById(1L)).thenReturn(Optional.of(store));
        Product product = new Product();
        product.setHasExpirationDate(false);
        product.setExpirationDate(LocalDate.now());
        productService.saveProductForStore(product, 1L);
        assertNull(product.getExpirationDate());
    }

    // getProductByNameAndStoreId()
    @Test
    void getProductByNameAndStoreId_ShouldReturnProduct() {
        Product p = new Product();
        when(productRepository.findByNameAndStoreId("Мляко", 1L)).thenReturn(Optional.of(p));
        Optional<Product> result = productService.getProductByNameAndStoreId("Мляко", 1L);
        assertTrue(result.isPresent());
    }

    @Test
    void getProductByNameAndStoreId_ShouldReturnEmptyIfNotFound() {
        when(productRepository.findByNameAndStoreId(any(), any())).thenReturn(Optional.empty());
        Optional<Product> result = productService.getProductByNameAndStoreId("Хляб", 1L);
        assertFalse(result.isPresent());
    }

    @Test
    void getProductByNameAndStoreId_ShouldCallRepository() {
        productService.getProductByNameAndStoreId("Яйца", 1L);
        verify(productRepository).findByNameAndStoreId("Яйца", 1L);
    }

    // checkProductQuantity()
    @Test
    void checkProductQuantity_ShouldNotThrowIfSufficient() {
        Product p = new Product(); p.setQuantity(10);
        when(productRepository.findByNameAndStoreId("Ориз", 1L)).thenReturn(Optional.of(p));
        assertDoesNotThrow(() -> productService.checkProductQuantity("Ориз", 1L, 5));
    }

    @Test
    void checkProductQuantity_ShouldThrowIfInsufficient() {
        Product p = new Product(); p.setQuantity(2);
        when(productRepository.findByNameAndStoreId("Боб", 1L)).thenReturn(Optional.of(p));
        assertThrows(RuntimeException.class, () -> productService.checkProductQuantity("Боб", 1L, 5));
    }

    @Test
    void checkProductQuantity_ShouldThrowIfProductNotFound() {
        when(productRepository.findByNameAndStoreId(any(), any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> productService.checkProductQuantity("Сок", 1L, 1));
    }

    // getAvailableQuantity()
    @Test
    void getAvailableQuantity_ShouldReturnQuantity() {
        Product p = new Product(); p.setQuantity(8);
        when(productRepository.findByNameAndStoreId("Фанта", 1L)).thenReturn(Optional.of(p));
        int qty = productService.getAvailableQuantity("Фанта", 1L);
        assertEquals(8, qty);
    }

    @Test
    void getAvailableQuantity_ShouldThrowIfNotFound() {
        when(productRepository.findByNameAndStoreId(any(), any())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> productService.getAvailableQuantity("Чипс", 1L));
    }

    @Test
    void getAvailableQuantity_ShouldCallRepository() {
        when(productRepository.findByNameAndStoreId("Фанта", 10L))
                .thenReturn(Optional.of(new Product()));

        productService.getAvailableQuantity("Фанта", 10L);
        verify(productRepository).findByNameAndStoreId("Фанта", 10L);
    }

    // getProductsWithPrices()
    @Test
    void getProductsWithPrices_ShouldReturnCorrectMap() {
        Product p = new Product(null, "Обувки", 100.0, 1, ProductCategory.NON_FOOD, false, null, null, false);
        Store s = new Store(); s.setId(1L);
        StoreSettings settings = new StoreSettings(null, 0, 20, 0, 0);
        s.setStoreSettings(settings);
        when(productRepository.findByStoreIdAndDeletedFalse(1L)).thenReturn(List.of(p));
        Map<Product, BigDecimal> result = productService.getProductsWithPrices(s);
        assertEquals(1, result.size());
        assertEquals(BigDecimal.valueOf(120.00).setScale(2), result.get(p));
    }

    @Test
    void getProductsWithPrices_ShouldReturnEmptyMapIfNoProducts() {
        Store s = new Store(); s.setId(1L);
        s.setStoreSettings(new StoreSettings(null, 0, 0, 0, 0));
        when(productRepository.findByStoreIdAndDeletedFalse(1L)).thenReturn(Collections.emptyList());
        Map<Product, BigDecimal> result = productService.getProductsWithPrices(s);
        assertTrue(result.isEmpty());
    }

    @Test
    void getProductsWithPrices_ShouldUseStoreSettings() {
        Product p = new Product(null, "Сок", 100.0, 1, ProductCategory.FOOD, false, null, null, false);
        Store store = new Store(); store.setId(1L);
        store.setStoreSettings(new StoreSettings(null, 25, 0, 0, 0));
        when(productRepository.findByStoreIdAndDeletedFalse(1L)).thenReturn(List.of(p));
        Map<Product, BigDecimal> result = productService.getProductsWithPrices(store);
        assertEquals(BigDecimal.valueOf(125.00).setScale(2), result.get(p));
    }

    // findById()
    @Test
    void findById_ShouldReturnProductIfFound() {
        Product p = new Product();
        when(productRepository.findById(1L)).thenReturn(Optional.of(p));
        Product result = productService.findById(1L);
        assertEquals(p, result);
    }

    @Test
    void findById_ShouldThrowIfNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> productService.findById(99L));
    }

    @Test
    void findById_ShouldCallRepository() {
        Product product = new Product();
        product.setId(7L);
        when(productRepository.findById(7L)).thenReturn(Optional.of(product));

        productService.findById(7L);
        verify(productRepository).findById(7L);
    }
}
