package com.backend.dto;

import java.time.LocalDate;

public class DailyStatisticsWithDate {
    private LocalDate date;
    private DailyStatistics statistics;

    public DailyStatisticsWithDate(LocalDate date, DailyStatistics statistics) {
        this.date = date;
        this.statistics = statistics;
    }

    public LocalDate getDate() { return date; }
    public DailyStatistics getStatistics() { return statistics; }
}