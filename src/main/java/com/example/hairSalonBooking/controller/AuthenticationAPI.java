
package com.example.hairSalonBooking.controller;


import com.example.hairSalonBooking.entity.Account;
import com.example.hairSalonBooking.model.request.IntrospectRequest;
import com.example.hairSalonBooking.model.response.AccountResponse;
import com.example.hairSalonBooking.model.request.LoginRequest;
import com.example.hairSalonBooking.model.request.RegisterRequest;
import com.example.hairSalonBooking.model.response.ApiResponse;
import com.example.hairSalonBooking.model.response.AuthenticationResponse;
import com.example.hairSalonBooking.model.response.IntrospectResponse;
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
    ApiResponse <AccountResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        var result = authenticationService.register(registerRequest);
        return  ApiResponse.<AccountResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/login")
    ApiResponse<AuthenticationResponse>login(@Valid @RequestBody LoginRequest loginRequest) {
        var result = authenticationService.login(loginRequest);
        return  ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @GetMapping("/account")
    List<Account>getAllAccount() {

        return authenticationService.getAllAccount();
    }




}
