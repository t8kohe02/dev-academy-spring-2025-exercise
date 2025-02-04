package com.backend.dto;

import java.time.LocalDate;

public class DailyChartStatistics {
    private String date;
    private double totalConsumption;
    private double totalProduction;
    private double averagePrice;

    public DailyChartStatistics(LocalDate date, double totalConsumption, double totalProduction, double averagePrice) {
        this.date = date.toString();
        this.totalConsumption = totalConsumption;
        this.totalProduction = totalProduction;
        this.averagePrice = averagePrice;
    }

    public String getDate() { return date; }
    public double getTotalConsumption() { return totalConsumption; }
    public double getTotalProduction() { return totalProduction; }
    public double getAveragePrice() { return averagePrice; }
    
}
