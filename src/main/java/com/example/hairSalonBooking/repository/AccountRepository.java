package com.example.hairSalonBooking.repository;

import com.example.hairSalonBooking.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

    // Data JPA
    //ORM  : object realationship mapping

    Account findAccountByUsername(String username);
    Account findAccountByEmail(String email);
    Account findAccountByAccountid(Long accountid);
    Account deleteAccountByAccountid(Long accountid);
}
