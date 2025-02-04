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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ElectricityDataService {

    @Autowired
    private ElectricityDataRepository repository;

    // Method to fetch all distinct dates (with pagination) and their corresponding daily statistics
    public Page<DailyStatisticsWithDate> getAllDailyStatistics(Pageable pageable) {
        
        Page<LocalDate> dates = repository.findDistinctDateByOrderByDateDesc(pageable);

        
        return dates.map(date -> {
            DailyStatistics statistics = calculateDailyStatistics(date);
            return new DailyStatisticsWithDate(date, statistics);
        });
    }

    // Method to calculate daily statistics
    public DailyStatistics calculateDailyStatistics(LocalDate date) {
        // Fetch all data for the specific date
        List<ElectricityData> dataForDay = repository.findByDate(date);
        
        BigDecimal totalConsumption = BigDecimal.ZERO;
        BigDecimal totalProduction = BigDecimal.ZERO;
        BigDecimal totalPrice = BigDecimal.ZERO;
        long longestNegativePriceDuration = 0;
        long currentNegativeDuration = 0;

        for (ElectricityData data : dataForDay) {
            BigDecimal hourlyPrice = (data.getHourlyPrice() != null) ? data.getHourlyPrice() : BigDecimal.ZERO;
            BigDecimal consumptionAmount = (data.getConsumptionAmount() != null) ? data.getConsumptionAmount() : BigDecimal.ZERO;
            BigDecimal productionAmount = (data.getProductionAmount() != null) ? data.getProductionAmount() : BigDecimal.ZERO;

            totalConsumption = totalConsumption.add(consumptionAmount);
            totalProduction = totalProduction.add(productionAmount);
            totalPrice = totalPrice.add(hourlyPrice);

            if (hourlyPrice.compareTo(BigDecimal.ZERO) < 0) {
                currentNegativeDuration++;
            } else {
                longestNegativePriceDuration = Math.max(longestNegativePriceDuration, currentNegativeDuration);
                currentNegativeDuration = 0;
            }
        }

        longestNegativePriceDuration = Math.max(longestNegativePriceDuration, currentNegativeDuration);

        BigDecimal averagePrice = dataForDay.isEmpty() ? BigDecimal.ZERO : totalPrice.divide(BigDecimal.valueOf(dataForDay.size()), RoundingMode.HALF_UP);

        return new DailyStatistics(totalConsumption, totalProduction, averagePrice, longestNegativePriceDuration);
    }

    public List<DailyChartStatistics> getDailyStatisticsForChart(String month) {
        YearMonth yearMonth = YearMonth.parse(month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<ElectricityData> data = repository.findByDateBetween(startDate, endDate);

        Map<LocalDate, List<ElectricityData>> groupedByDate = data.stream()
            .collect(Collectors.groupingBy(ElectricityData::getDate));

        return groupedByDate.entrySet().stream().map(entry -> {
            LocalDate date = entry.getKey();
            List<ElectricityData> dayData = entry.getValue();

            BigDecimal totalConsumption = dayData.stream()
                .map(ElectricityData::getConsumptionAmount)
                .map(val -> Optional.ofNullable(val).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add); 

            BigDecimal totalProduction = dayData.stream()
                .map(ElectricityData::getProductionAmount)
                .map(val -> Optional.ofNullable(val).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add); 

            double avgPrice = dayData.stream()
                .map(ElectricityData::getHourlyPrice)
                .filter(Objects::nonNull)
                .mapToDouble(BigDecimal::doubleValue)
                .average().orElse(0); 

            return new DailyChartStatistics(date, 
                totalConsumption.doubleValue(), 
                totalProduction.doubleValue(), 
                avgPrice);
        }).collect(Collectors.toList());
    }   
    
}
