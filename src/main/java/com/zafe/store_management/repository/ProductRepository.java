package com.zafe.store_management.repository;

import com.zafe.store_management.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByStoreId(Long storeId);
    Optional<Product> findByNameAndStoreId(String name, Long storeId);
    List<Product> findByStoreIdAndDeletedFalse(Long storeId);
    Optional<Product> findById(Long id);
}
