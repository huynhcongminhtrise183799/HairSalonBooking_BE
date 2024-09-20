package com.example.hairSalonBooking.service;


import com.example.hairSalonBooking.entity.Account;
import com.example.hairSalonBooking.exception.DuplicateEntity;
import com.example.hairSalonBooking.exception.EntityNotFoundException;
import com.example.hairSalonBooking.model.AccountResponse;
import com.example.hairSalonBooking.model.LoginRequest;
import com.example.hairSalonBooking.model.RegisterRequest;
import com.example.hairSalonBooking.repository.AccountRepository;
import jakarta.validation.ConstraintViolationException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthenticationService implements UserDetailsService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AuthenticationManager authenticationManager;

    public AccountResponse register(RegisterRequest registerRequest) {
        Account account = modelMapper.map(registerRequest, Account.class);
        try{
            String originPassword = account.getPassword(); // goi
            account.setPassword(passwordEncoder.encode(originPassword));// dinh dang
            Account newAccount = accountRepository.save(account);
            return modelMapper.map(newAccount, AccountResponse.class);
        }catch(Exception e) {
            if(e.getMessage().contains(account.getUsername())) {
                throw new DuplicateEntity("Duplicate Username!");
            }else if(e.getMessage().contains(account.getEmail())) {
                throw new DuplicateEntity("Duplicate email!");
            }else {
                throw new DuplicateEntity("Duplicate phone!");
            }
        }
    }


    public AccountResponse login(LoginRequest loginRequest) { // xac minh xem username va password co trong database hay khong
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()));

            // => tai khoan co ton tai
            Account account = (Account) authentication.getPrincipal();

            return modelMapper.map(account, AccountResponse.class);

        }catch(Exception e) {
            throw new EntityNotFoundException("Invalid username or password!");
        }
    }


    public List<Account> getAllAccount() {
        List<Account> accounts = accountRepository.findAll();
        return accounts;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return accountRepository.findAccountByUsername(username);
    } // Định nghĩa cho mình biet cach lay Username
}
