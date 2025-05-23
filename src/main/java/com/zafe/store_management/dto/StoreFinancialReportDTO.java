package com.zafe.store_management.dto;

import java.math.BigDecimal;

public class StoreFinancialReportDTO {
    private final BigDecimal totalDeliveryCost;
    private final BigDecimal totalSalaries;
    private final BigDecimal totalIncome;
    private final BigDecimal profit;

    public StoreFinancialReportDTO(BigDecimal totalDeliveryCost, BigDecimal totalSalaries, BigDecimal totalIncome, BigDecimal profit) {
        this.totalDeliveryCost = totalDeliveryCost;
        this.totalSalaries = totalSalaries;
        this.totalIncome = totalIncome;
        this.profit = profit;
    }

    // Getters
    public BigDecimal getTotalDeliveryCost() { return totalDeliveryCost; }
    public BigDecimal getTotalSalaries() { return totalSalaries; }
    public BigDecimal getTotalIncome() { return totalIncome; }
    public BigDecimal getProfit() { return profit; }
}
