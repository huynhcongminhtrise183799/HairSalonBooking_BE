package com.example.hairSalonBooking.repository;

import com.example.hairSalonBooking.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Account,Long> {
    Account findAccountByAccountid(long AccountID);
    Account findAccountByUsername(String username);
}
