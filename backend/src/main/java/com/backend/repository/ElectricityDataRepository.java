package com.backend.repository;

import com.backend.model.ElectricityData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ElectricityDataRepository extends JpaRepository<ElectricityData, Integer> {
    List<ElectricityData> findByDate(LocalDate date);
}
