package com.example.hairSalonBooking.repository;

import com.example.hairSalonBooking.entity.SalonService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ServiceRepository extends JpaRepository<SalonService,Long> {
    Optional<SalonService> findByServiceName(String serviceName);
    Optional<SalonService> findByServiceId(long serviceId);
    @Query("SELECT s FROM SalonService s WHERE LOWER(s.serviceName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<SalonService> findByServiceNameContainingIgnoreCase(@Param("keyword") String keyword);
    @Query(value = "select ss.* from salon_service ss\n" +
            "inner join booking_detail bd\n" +
            "on ss.service_id = bd.service_id\n" +
            "where bd.booking_id = ?1 ",nativeQuery = true)
    List<SalonService> getServiceForBooking(long bookingId);

    @Query(value = "select sec_to_time(sum(time_to_sec(ss.duration))) from salon_service ss\n" +
            "inner join booking_detail bd\n" +
            "on ss.service_id = bd.service_id\n" +
            "where bd.booking_id = ?1 ",nativeQuery = true)
    LocalTime  getTotalTime(long bookingId);
    @Query(value = "select * from salon_service s where s.service_id = ?1",nativeQuery = true)
    SalonService getServiceById(long id);
}
