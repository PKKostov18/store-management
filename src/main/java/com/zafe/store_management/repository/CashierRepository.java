package com.zafe.store_management.repository;

import com.zafe.store_management.model.Cashier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CashierRepository extends JpaRepository<Cashier, Long> {
    @Query("SELECT c FROM Cashier c WHERE c.store.id = :storeId AND c NOT IN (SELECT cr.cashier FROM CashRegister cr WHERE cr.cashier IS NOT NULL)")
    List<Cashier> findByStoreIdAndCashRegisterIsNull(@Param("storeId") Long storeId);
}
