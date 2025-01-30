package com.backend.service;

import com.backend.model.ElectricityData;
import com.backend.model.DailyStatistics;
import com.backend.repository.ElectricityDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class ElectricityDataService {

    @Autowired
    private ElectricityDataRepository repository;

    // Calculate daily statistics
    public DailyStatistics getDailyStatistics(LocalDate date) {
        List<ElectricityData> dataForDay = repository.findByDate(date);

        BigDecimal totalConsumption = BigDecimal.ZERO;
        BigDecimal totalProduction = BigDecimal.ZERO;
        BigDecimal totalPrice = BigDecimal.ZERO;
        long longestNegativePriceDuration = 0;
        long currentNegativeDuration = 0;

        for (ElectricityData data : dataForDay) {
            totalConsumption = totalConsumption.add(data.getConsumptionAmount() != null ? data.getConsumptionAmount() : BigDecimal.ZERO);
            totalProduction = totalProduction.add(data.getProductionAmount() != null ? data.getProductionAmount() : BigDecimal.ZERO);
            totalPrice = totalPrice.add(data.getHourlyPrice() != null ? data.getHourlyPrice() : BigDecimal.ZERO);

            // Track the longest consecutive negative price period
            if (data.getHourlyPrice().compareTo(BigDecimal.ZERO) < 0) {
                currentNegativeDuration++;
            } else {
                longestNegativePriceDuration = Math.max(longestNegativePriceDuration, currentNegativeDuration);
                currentNegativeDuration = 0;
            }
        }

        // Ensure to update the longest negative duration if last period was negative
        longestNegativePriceDuration = Math.max(longestNegativePriceDuration, currentNegativeDuration);

        BigDecimal averagePrice = dataForDay.isEmpty() ? BigDecimal.ZERO : totalPrice.divide(BigDecimal.valueOf(dataForDay.size()), RoundingMode.HALF_UP);

        return new DailyStatistics(totalConsumption, totalProduction, averagePrice, longestNegativePriceDuration);
    }
}
