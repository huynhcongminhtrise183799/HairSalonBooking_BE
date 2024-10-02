package com.example.hairSalonBooking.repository;

import com.example.hairSalonBooking.entity.SalonService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository extends JpaRepository<SalonService,Long> {
    Optional<SalonService> findByServiceName(String serviceName);
    Optional<SalonService> findByServiceId(long serviceId);
    @Query("SELECT s FROM SalonService s WHERE LOWER(s.serviceName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<SalonService> findByServiceNameContainingIgnoreCase(@Param("keyword") String keyword);
}
