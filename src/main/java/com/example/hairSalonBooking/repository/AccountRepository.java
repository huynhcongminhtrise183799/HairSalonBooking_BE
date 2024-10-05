package com.example.hairSalonBooking.repository;

import com.example.hairSalonBooking.entity.Account;
import com.example.hairSalonBooking.entity.Slot;
import com.example.hairSalonBooking.enums.Role;
import jakarta.transaction.Transactional;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

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

    @Query(value = "select ac.* from account ac\n" +
            "inner join specific_skill ss\n" +
            "on ac.accountid = ss.account_id\n" +
            "inner join stylist_schedule ssch\n" +
            "on ac.accountid = ssch.account_id\n" +
            "where ss.skill_id = ?1 and ac.salon_id = ?2 ",nativeQuery = true)
    Set<Account> getAccountBySkill(long skillId,long salonId);
    List<Account> findByRoleAndIsDeletedFalse(Role role);

    @Query(value = "select distinct ac.* from account ac\n" +
            "            inner join specific_skill sk\n" +
            "            on ac.accountid = sk.account_id\n" +
            "            inner join salon_service ss\n" +
            "            on sk.skill_id = ss.skill_id\n" +
            "            inner join stylist_schedule ssch\n" +
            "            on ac.accountid = ssch.account_id\n" +
            "            where ss.service_id = ?1 and ssch.working_day = ?2 and ac.salon_id = ?3",nativeQuery = true)
    List<Account> getStylistForBooking(long serviceId, LocalDate workingDay, long salonId);
}

