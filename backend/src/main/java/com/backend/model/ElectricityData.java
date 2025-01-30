package com.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "electricitydata")
public class ElectricityData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDate date;

    private LocalDateTime startTime;

    private BigDecimal productionAmount;

    private BigDecimal consumptionAmount;

    private BigDecimal hourlyPrice;

    // Constructors
    public ElectricityData() {}

    public ElectricityData(LocalDate date, LocalDateTime startTime, BigDecimal productionAmount,
                           BigDecimal consumptionAmount, BigDecimal hourlyPrice) {
        this.date = date;
        this.startTime = startTime;
        this.productionAmount = productionAmount;
        this.consumptionAmount = consumptionAmount;
        this.hourlyPrice = hourlyPrice;
    }

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public BigDecimal getProductionAmount() { return productionAmount; }
    public void setProductionAmount(BigDecimal productionAmount) { this.productionAmount = productionAmount; }

    public BigDecimal getConsumptionAmount() { return consumptionAmount; }
    public void setConsumptionAmount(BigDecimal consumptionAmount) { this.consumptionAmount = consumptionAmount; }

    public BigDecimal getHourlyPrice() { return hourlyPrice; }
    public void setHourlyPrice(BigDecimal hourlyPrice) { this.hourlyPrice = hourlyPrice; }
}
