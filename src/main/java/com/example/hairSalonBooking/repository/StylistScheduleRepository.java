package com.example.hairSalonBooking.repository;

import com.example.hairSalonBooking.entity.StylistSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface StylistScheduleRepository extends JpaRepository<StylistSchedule,Long> {
    @Query(value = "select * from stylist_schedule ss where ss.account_id = ?1 " +
            "and ss.working_day = ?2",nativeQuery = true)
    StylistSchedule getScheduleId(long stylistId, LocalDate date);
}
