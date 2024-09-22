package com.example.hairSalonBooking.repository;

import com.example.hairSalonBooking.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
