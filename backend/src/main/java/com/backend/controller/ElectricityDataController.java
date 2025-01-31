package com.backend.controller;

import com.backend.dto.DailyStatistics;
import com.backend.dto.DailyStatisticsWithDate;
import com.backend.service.ElectricityDataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        return service.getDailyStatistics(LocalDate.parse(date));
    }

    // Endpoint to fetch all unique dates with daily statistics
    @GetMapping("/daily-stats/all")
    public List<DailyStatisticsWithDate> getAllDailyStatistics() {
        return service.getAllDailyStatistics();
    }
}