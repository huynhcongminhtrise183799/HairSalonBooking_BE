package com.example.hairSalonBooking.repository;

import com.example.hairSalonBooking.entity.StylistSchedule;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface StylistScheduleRepository extends JpaRepository<StylistSchedule,Long> {
    @Query(value = "select * from stylist_schedule ss where ss.account_id = ?1 " +
            "and ss.working_day = ?2",nativeQuery = true)
    StylistSchedule getScheduleId(long stylistId, LocalDate date);
    @Query(value = "select distinct ss.* from  stylist_schedule ss\n" +
            "inner join specific_stylist_schedule sss\n" +
            "on ss.stylist_schedule_id = sss.stylist_schedule_id\n" +
            "inner join account a\n" +
            "on ss.account_id = a.accountid\n" +
            "where a.salon_id = ?1 and ss.working_day = ?2",nativeQuery = true)
    List<StylistSchedule> getStylistScheduleByDayAndSalonId(long salonId, LocalDate date);

    StylistSchedule findByStylistScheduleId(long id);
    @Query(value = "DELETE FROM specific_stylist_schedule WHERE stylist_schedule_id = ?1",nativeQuery = true)
    @Modifying
    @Transactional
    void deleteSpecificSchedule(long id);
}
