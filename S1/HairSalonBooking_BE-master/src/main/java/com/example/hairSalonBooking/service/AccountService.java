package com.example.hairSalonBooking.service;

import com.example.hairSalonBooking.entity.Account;
import com.example.hairSalonBooking.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    public Account getAccountById(long accountId)
    {
        return accountRepository.findById(accountId).orElse(null);
    }
}
