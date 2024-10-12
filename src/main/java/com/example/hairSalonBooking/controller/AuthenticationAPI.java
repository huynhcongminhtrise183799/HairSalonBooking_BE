
package com.example.hairSalonBooking.controller;


import com.example.hairSalonBooking.entity.Account;

import com.example.hairSalonBooking.model.request.IntrospectRequest;

import com.example.hairSalonBooking.model.response.AccountResponse;
import com.example.hairSalonBooking.model.request.LoginRequest;
import com.example.hairSalonBooking.model.request.RegisterRequest;
import com.example.hairSalonBooking.model.response.AuthenticationResponse;

import com.example.hairSalonBooking.model.response.IntrospectResponse;

import com.example.hairSalonBooking.model.response.AccountPageResponse;

import com.example.hairSalonBooking.service.AuthenticationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;



import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.text.ParseException;

import java.util.List;

@RestController
@RequestMapping("/api") // giảm bớt đường dẫn


@CrossOrigin("http://localhost:3000/")
@SecurityRequirement(name = "api")

public class AuthenticationAPI {


    // DI :Dependency Injection


    @Autowired // mình sử dụng để gọi thằng service phục vụ
    AuthenticationService authenticationService;



    @PostMapping("/register")
    public ResponseEntity register(@Valid @RequestBody RegisterRequest registerRequest) {
        AccountResponse newAccount = authenticationService.register(registerRequest);
        return ResponseEntity.ok(newAccount);
    }

    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthenticationResponse newAccount = authenticationService.login(loginRequest);
        return ResponseEntity.ok(newAccount);
    }

    @GetMapping("/account")
    public ResponseEntity getAllAccount() {
        List<Account> accounts = authenticationService.getAllAccount();
        return ResponseEntity.ok(accounts);
    }



    @GetMapping("/account/page")
    public ResponseEntity getAllAccountCustomer(@RequestParam int page, @RequestParam int size) {
        AccountPageResponse customerPageResponse = authenticationService.getAllAccountCustomer(page, size);
        return ResponseEntity.ok(customerPageResponse);
    }



}