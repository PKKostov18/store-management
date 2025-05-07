package com.zafe.store_management.service;

import com.zafe.store_management.model.Product;
import com.zafe.store_management.model.ProductCategory;
import com.zafe.store_management.model.StoreSettings;
import com.zafe.store_management.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
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
}
