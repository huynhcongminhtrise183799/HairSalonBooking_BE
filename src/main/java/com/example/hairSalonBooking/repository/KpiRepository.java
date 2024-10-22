package com.example.hairSalonBooking.repository;

import com.example.hairSalonBooking.entity.Kpi;
import com.example.hairSalonBooking.entity.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface KpiRepository extends JpaRepository<Kpi, Long> {
//     @Query("SELECT k FROM Kpi k JOIN k.level.accounts a WHERE a.accountid = :stylistId AND k.level.levelid = :levelId")
//     Kpi findByStylistIdAndLevel(@Param("stylistId") Long stylistId, @Param("levelId") Long levelId);

     @Query("SELECT k FROM Kpi k JOIN k.level l JOIN l.accounts a WHERE a.accountid = :stylistId AND l.levelid = :levelId")
     List<Kpi> findByStylistIdAndLevel(@Param("stylistId") Long stylistId, @Param("levelId") Long levelId);


     @Query("SELECT MAX(k.revenueFrom) FROM Kpi k JOIN k.level.accounts a WHERE a.accountid = :stylistId AND k.level.levelid = :levelId")
     Double findMaxRevenueByStylistIdAndLevel(@Param("stylistId") Long stylistId, @Param("levelId") Long levelId);

}
