package com.example.hairSalonBooking.service;


import com.example.hairSalonBooking.entity.Account;
import com.example.hairSalonBooking.exception.AppException;
import com.example.hairSalonBooking.exception.ErrorCode;
import com.example.hairSalonBooking.model.response.AccountResponse;
import com.example.hairSalonBooking.model.request.LoginRequest;
import com.example.hairSalonBooking.model.request.RegisterRequest;
import com.example.hairSalonBooking.repository.AccountRepository;
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
        if(!registerRequest.getConfirmpassword().equals(registerRequest.getPassword())){
            throw new RuntimeException("Password not match");
        }
        try{
            String originPassword = account.getPassword(); // goi
            account.setPassword(passwordEncoder.encode(originPassword));// dinh dang
            Account newAccount = accountRepository.save(account);
            return modelMapper.map(newAccount, AccountResponse.class);
        }catch(Exception e) {
            if(e.getMessage().contains(account.getUsername())) {
                throw new AppException(ErrorCode.USERNAME_EXISTED);
            }else if(e.getMessage().contains(account.getEmail())) {
                throw new AppException(ErrorCode.EMAIL_EXISTED);
            }else {
                throw new AppException(ErrorCode.Phone_EXISTED);
            }
        }
    }


   public AuthenticationResponse login(LoginRequest loginRequest) { // xac minh xem username va password co trong database hay khong
        Account account; // Declare account here to make it accessible later
        try {
            // Authenticate the username and password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // Get the authenticated account from the authentication object
            account = (Account) authentication.getPrincipal();

        }catch(Exception e) {
            throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
        }
        var token = generateToken(loginRequest.getUsername());
        AuthenticationResponse response = modelMapper.map(account, AuthenticationResponse.class);
        response.setToken(token);
        response.setSuccess(true);

        return response;
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
