package com.example.hairSalonBooking.repository;

import com.example.hairSalonBooking.entity.SalaryRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaryRecordRepository extends JpaRepository<SalaryRecord, Long> {
}
