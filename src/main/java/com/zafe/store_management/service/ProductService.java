package com.zafe.store_management.service;

import com.zafe.store_management.exception.InsufficientQuantityException;
import com.zafe.store_management.exception.ProductNotFoundException;
import com.zafe.store_management.exception.StoreNotFoundException;
import com.zafe.store_management.model.Product;
import com.zafe.store_management.model.ProductCategory;
import com.zafe.store_management.model.Store;
import com.zafe.store_management.model.StoreSettings;
import com.zafe.store_management.repository.ProductRepository;
import com.zafe.store_management.repository.StoreRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;

    public ProductService(ProductRepository productRepository, StoreRepository storeRepository) {
        this.productRepository = productRepository;
        this.storeRepository = storeRepository;
    }

    public void save(Product product) {
        productRepository.save(product);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public BigDecimal calculateSellingPrice(Product product, StoreSettings settings) {
        BigDecimal markup = BigDecimal.ZERO;

        if (product.getCategory() == ProductCategory.FOOD) {
            markup = BigDecimal.valueOf(settings.getFoodMarkupPercentage()).divide(BigDecimal.valueOf(100));
        } else {
            markup = BigDecimal.valueOf(settings.getNonFoodMarkupPercentage()).divide(BigDecimal.valueOf(100));
        }

        BigDecimal deliveryPrice = BigDecimal.valueOf(product.getDeliveryPrice());
        BigDecimal priceWithMarkup = deliveryPrice.add(deliveryPrice.multiply(markup));

        if (product.isHasExpirationDate() && product.getExpirationDate() != null) {
            long daysToExpire = ChronoUnit.DAYS.between(LocalDate.now(), product.getExpirationDate());
            if (daysToExpire >= 0 && daysToExpire <= settings.getDaysBeforeExpirationForDiscount()) {
                BigDecimal discount = BigDecimal.valueOf(settings.getDiscountPercentage()).divide(BigDecimal.valueOf(100));
                return priceWithMarkup.subtract(priceWithMarkup.multiply(discount)).setScale(2, RoundingMode.HALF_UP);
            }
        }

        return priceWithMarkup.setScale(2, RoundingMode.HALF_UP);
    }

    public List<Product> getProductsByStoreId(Long storeId) {
        return productRepository.findByStoreId(storeId);
    }

    public void saveProductForStore(Product product, Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreNotFoundException(storeId));

        product.setStore(store);

        if (!product.isHasExpirationDate()) {
            product.setExpirationDate(null);
        }

        productRepository.save(product);
    }

    public Optional<Product> getProductByNameAndStoreId(String name, Long storeId) {
        return productRepository.findByNameAndStoreId(name, storeId);
    }

    public void checkProductQuantity(String productName, Long storeId, int requestedQuantity) {
        Product product = getProductByNameAndStoreId(productName, storeId)
                .orElseThrow(() -> new ProductNotFoundException(productName, storeId));

        if (requestedQuantity > product.getQuantity()) {
            throw new InsufficientQuantityException(productName, requestedQuantity, product.getQuantity());
        }
    }

    public int getAvailableQuantity(String productName, Long storeId) {
        return getProductByNameAndStoreId(productName, storeId)
                .map(Product::getQuantity)
                .orElseThrow(() -> new ProductNotFoundException(productName, storeId));
    }

    public Map<Product, BigDecimal> getProductsWithPrices(Store store) {
        List<Product> products = getProductsByStoreId(store.getId());
        StoreSettings settings = store.getStoreSettings();

        Map<Product, BigDecimal> productPrices = new LinkedHashMap<>();
        for (Product product : products) {
            BigDecimal sellingPrice = calculateSellingPrice(product, settings);
            productPrices.put(product, sellingPrice);
        }

        return productPrices;
    }
}
