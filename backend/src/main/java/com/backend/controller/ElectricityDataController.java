package com.backend.controller;

import com.backend.service.ElectricityDataService;
import com.backend.model.DailyStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/electricity")
public class ElectricityDataController {

    @Autowired
    private ElectricityDataService service;

    // Endpoint to get daily statistics
    @GetMapping("/daily-stats/{date}")
    public DailyStatistics getDailyStatistics(@PathVariable String date) {
        return service.getDailyStatistics(LocalDate.parse(date));
    }
}