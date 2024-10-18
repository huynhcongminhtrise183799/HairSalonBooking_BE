package com.example.hairSalonBooking.repository;

import com.example.hairSalonBooking.entity.Kpi;
import com.example.hairSalonBooking.entity.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface KpiRepository extends JpaRepository<Kpi, Long> {
    @Query("SELECT k FROM Kpi k WHERE k.level = :level AND k.yearAndMonth = :yearAndMonth")
    Kpi findByLevelAndYearAndMonth(Level level, String yearAndMonth);
}