package com.example.hairSalonBooking.repository;

import com.example.hairSalonBooking.entity.Account;
import com.example.hairSalonBooking.entity.Slot;
import com.example.hairSalonBooking.enums.Role;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {

    // Data JPA
    //ORM  : object realationship mapping

    Account findAccountByUsername(String username);
    Account findAccountByEmail(String email);
    Account findAccountByAccountid(Long accountid);

    @Query("SELECT ac FROM Account ac WHERE ac.role = com.example.hairSalonBooking.enums.Role.STAFF")
    @Transactional
    List<Account> getAccountsByRoleSTAFF();
    @Query("SELECT ac FROM Account ac WHERE ac.role = com.example.hairSalonBooking.enums.Role.BRANCH_MANAGER")
    @Transactional
    List<Account> getAccountsByRoleManager();


    List<Account> findByRole(Role role);



    List<Account> findByRoleAndIsDeletedFalse(Role role);
}

