package com.backend.model;

import java.math.BigDecimal;

public class DailyStatistics {

    private BigDecimal totalConsumption;
    private BigDecimal totalProduction;
    private BigDecimal averagePrice;
    private long longestNegativePriceDuration;

    public DailyStatistics(BigDecimal totalConsumption, BigDecimal totalProduction, BigDecimal averagePrice, long longestNegativePriceDuration) {
        this.totalConsumption = totalConsumption;
        this.totalProduction = totalProduction;
        this.averagePrice = averagePrice;
        this.longestNegativePriceDuration = longestNegativePriceDuration;
    }

    public BigDecimal getTotalConsumption() { return totalConsumption; }
    public BigDecimal getTotalProduction() { return totalProduction; }
    public BigDecimal getAveragePrice() { return averagePrice; }
    public long getLongestNegativePriceDuration() { return longestNegativePriceDuration; }
}
