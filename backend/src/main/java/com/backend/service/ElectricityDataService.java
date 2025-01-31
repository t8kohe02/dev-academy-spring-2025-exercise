package com.backend.service;

import com.backend.dto.DailyStatistics;
import com.backend.dto.DailyStatisticsWithDate;
import com.backend.model.ElectricityData;
import com.backend.repository.ElectricityDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Service
public class ElectricityDataService {

    @Autowired
    private ElectricityDataRepository repository;

    // Method to find all unique dates with corresponding daily statistics in descending order
    public List<DailyStatisticsWithDate> getAllDailyStatistics() {
        List<LocalDate> uniqueDates = repository.findDistinctDateByOrderByDateDesc();
        List<DailyStatisticsWithDate> statisticsList = new ArrayList<>();

        for (LocalDate date : uniqueDates) {
            // Retrieve daily statistics for each date
            DailyStatistics dailyStatistics = getDailyStatistics(date);
            statisticsList.add(new DailyStatisticsWithDate(date, dailyStatistics));
        }

        return statisticsList;
    }    


    // Calculate daily statistics
    public DailyStatistics getDailyStatistics(LocalDate date) {
        List<ElectricityData> dataForDay = repository.findByDate(date);
    
        BigDecimal totalConsumption = BigDecimal.ZERO;
        BigDecimal totalProduction = BigDecimal.ZERO;
        BigDecimal totalPrice = BigDecimal.ZERO;
        long longestNegativePriceDuration = 0;
        long currentNegativeDuration = 0;
    
        for (ElectricityData data : dataForDay) {
            // Safely retrieve the value of hourlyPrice, default to BigDecimal.ZERO if null
            BigDecimal hourlyPrice = (data.getHourlyPrice() != null) ? data.getHourlyPrice() : BigDecimal.ZERO;
    
            // Safely handle consumptionAmount and productionAmount being null
            BigDecimal consumptionAmount = (data.getConsumptionAmount() != null) ? data.getConsumptionAmount() : BigDecimal.ZERO;
            BigDecimal productionAmount = (data.getProductionAmount() != null) ? data.getProductionAmount() : BigDecimal.ZERO;
    
            // Add values to totals
            totalConsumption = totalConsumption.add(consumptionAmount);
            totalProduction = totalProduction.add(productionAmount);
            totalPrice = totalPrice.add(hourlyPrice);
    
            // Track the longest consecutive negative price period
            if (hourlyPrice.compareTo(BigDecimal.ZERO) < 0) {
                currentNegativeDuration++;
            } else {
                longestNegativePriceDuration = Math.max(longestNegativePriceDuration, currentNegativeDuration);
                currentNegativeDuration = 0;
            }
        }
    
        // Ensure to update the longest negative duration if last period was negative
        longestNegativePriceDuration = Math.max(longestNegativePriceDuration, currentNegativeDuration);
    
        // Calculate average price if there is data
        BigDecimal averagePrice = dataForDay.isEmpty() ? BigDecimal.ZERO : totalPrice.divide(BigDecimal.valueOf(dataForDay.size()), RoundingMode.HALF_UP);
    
        // Return the daily statistics
        return new DailyStatistics(totalConsumption, totalProduction, averagePrice, longestNegativePriceDuration);
    }
}
