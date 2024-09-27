package com.example.hairSalonBooking.repository;

import com.example.hairSalonBooking.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffRepository extends JpaRepository<StaffRepository, Long> {



    Account findAccountStaffByid(String id);

}

