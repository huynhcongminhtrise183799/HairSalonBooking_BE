package com.example.hairSalonBooking.repository;

import com.example.hairSalonBooking.entity.Shift;
import com.example.hairSalonBooking.model.response.ShiftResponse;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

public interface ShiftRepository extends JpaRepository<Shift,Long> {
    Shift findByShiftId(long id);
    @Query(value = "select distinct s.* from shift s \n" +
            "inner join specific_stylist_schedule sss\n" +
            "on s.shift_id = sss.shift_id\n" +
            "inner join booking b \n" +
            "on sss.stylist_schedule_id = b.stylist_schedule_id\n" +
            "inner join slot sl\n" +
            "on b.slot_id = sl.slotid\n" +
            "where (?1 between s.start_time and s.end_time) \n" +
            "or (?2 between s.start_time and s.end_time) and b.booking_id = ?3;",nativeQuery = true)
    List<Shift> getShiftForBooking(LocalTime timeStart, LocalTime timeEnd, long id);
    @Query(value = "select s.* from shift s\n" +
            "inner join specific_stylist_schedule sss\n" +
            "on s.shift_id = sss.shift_id\n" +
            "inner join stylist_schedule ssch\n" +
            "on sss.stylist_schedule_id = ssch.stylist_schedule_id\n" +
            "where ssch.account_id = ?1 and ssch.working_day = ?2",nativeQuery = true)
    List<Shift> getShiftsFromSpecificStylistSchedule(long id, LocalDate date);
}
