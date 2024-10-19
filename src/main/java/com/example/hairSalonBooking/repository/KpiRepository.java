package com.example.hairSalonBooking.repository;

import com.example.hairSalonBooking.entity.Kpi;
import com.example.hairSalonBooking.entity.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface KpiRepository extends JpaRepository<Kpi, Long> {
    @Query("SELECT k FROM Kpi k JOIN k.level.accounts a WHERE k.yearAndMonth = :yearAndMonth AND a.accountid = :stylistId")
    Kpi findByStylistIdAndYearAndMonth(@Param("stylistId") Long stylistId, @Param("yearAndMonth") String yearAndMonth);

}
