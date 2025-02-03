package com.backend.controller;

import com.backend.dto.DailyStatistics;
import com.backend.dto.DailyStatisticsWithDate;
import com.backend.dto.DailyChartStatistics;
import com.backend.service.ElectricityDataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/electricity")
public class ElectricityDataController {

    @Autowired
    private ElectricityDataService service;

    // Endpoint to get daily statistics for specific date
    @GetMapping("/daily-stats/{date}")
    public DailyStatistics getDailyStatistics(@PathVariable String date) {
        return service.calculateDailyStatistics(LocalDate.parse(date));
    }

    // Endpoint to fetch paginated daily statistics
    @GetMapping("/daily-stats/all")
    public Page<DailyStatisticsWithDate> getAllDailyStatistics(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return service.getAllDailyStatistics(pageRequest);
    }

    // Endpoint to fetch daily average statistics for chart
    @GetMapping("/chart/daily")
    public List<DailyChartStatistics> getDailyChartStatistics(@RequestParam(defaultValue = "2024-10") String month) {
        return service.getDailyStatisticsForChart(month);
    }
}