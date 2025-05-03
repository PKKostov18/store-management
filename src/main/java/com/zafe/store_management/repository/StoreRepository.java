package com.zafe.store_management.repository;

import com.zafe.store_management.model.Product;
import com.zafe.store_management.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> { }
