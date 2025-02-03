package com.backend.service;

import com.backend.dto.DailyStatistics;
import com.backend.dto.DailyStatisticsWithDate;
import com.backend.dto.DailyChartStatistics;
import com.backend.model.ElectricityData;
import com.backend.repository.ElectricityDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ElectricityDataService {

    @Autowired
    private ElectricityDataRepository repository;

    // Method to fetch all distinct dates (with pagination) and their corresponding daily statistics
    public Page<DailyStatisticsWithDate> getAllDailyStatistics(Pageable pageable) {
        // Fetch all distinct dates (this doesn't calculate any statistics)
        Page<LocalDate> dates = repository.findDistinctDateByOrderByDateDesc(pageable);

        // For each date, calculate the corresponding statistics
        return dates.map(date -> {
            DailyStatistics statistics = calculateDailyStatistics(date); // Calculate stats for the given date
            return new DailyStatisticsWithDate(date, statistics);
        });
    }

    // Method to calculate daily statistics (aggregates for each day)
    public DailyStatistics calculateDailyStatistics(LocalDate date) {
        // Fetch all data for the specific date
        List<ElectricityData> dataForDay = repository.findByDate(date);

        // Initialize totals
        BigDecimal totalConsumption = BigDecimal.ZERO;
        BigDecimal totalProduction = BigDecimal.ZERO;
        BigDecimal totalPrice = BigDecimal.ZERO;
        long longestNegativePriceDuration = 0;
        long currentNegativeDuration = 0;

        // Loop over the fetched data to compute aggregates
        for (ElectricityData data : dataForDay) {
            // Safely retrieve hourly price, consumption amount, and production amount
            BigDecimal hourlyPrice = (data.getHourlyPrice() != null) ? data.getHourlyPrice() : BigDecimal.ZERO;
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

        // Ensure to update the longest negative duration if the last period was negative
        longestNegativePriceDuration = Math.max(longestNegativePriceDuration, currentNegativeDuration);

        // Calculate average price if there is data
        BigDecimal averagePrice = dataForDay.isEmpty() ? BigDecimal.ZERO : totalPrice.divide(BigDecimal.valueOf(dataForDay.size()), RoundingMode.HALF_UP);

        // Return the calculated statistics for the given date
        return new DailyStatistics(totalConsumption, totalProduction, averagePrice, longestNegativePriceDuration);
    }

    public List<DailyChartStatistics> getDailyStatisticsForChart(String month) {
        YearMonth yearMonth = YearMonth.parse(month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // Fetch all data for the specified month
        List<ElectricityData> data = repository.findByDateBetween(startDate, endDate);

        // Group data by date
        Map<LocalDate, List<ElectricityData>> groupedByDate = data.stream()
            .collect(Collectors.groupingBy(ElectricityData::getDate));

        // Compute daily statistics
        return groupedByDate.entrySet().stream().map(entry -> {
            LocalDate date = entry.getKey();
            List<ElectricityData> dayData = entry.getValue();

            BigDecimal totalConsumption = dayData.stream()
                .map(ElectricityData::getConsumptionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add); // SUM

            BigDecimal totalProduction = dayData.stream()
                .map(ElectricityData::getProductionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add); // SUM

            double avgPrice = dayData.stream()
                .map(ElectricityData::getHourlyPrice)
                .mapToDouble(BigDecimal::doubleValue)
                .average().orElse(0); // AVERAGE

            return new DailyChartStatistics(date, 
                totalConsumption.doubleValue(), 
                totalProduction.doubleValue(), 
                avgPrice);
        }).collect(Collectors.toList());
    }   
    
}
