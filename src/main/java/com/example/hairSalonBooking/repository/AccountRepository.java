package com.example.hairSalonBooking.repository;

import com.example.hairSalonBooking.entity.Account;
import com.example.hairSalonBooking.entity.Slot;
import com.example.hairSalonBooking.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {

    // Data JPA
    //ORM  : object realationship mapping

    Account findAccountByUsername(String username);
    Account findAccountByEmail(String email);
    Account findAccountByAccountid(Long accountid);
    List<Account> findByRole(Role role);
    List<Account> findByRoleAndIsDeletedFalse(Role role);
}

