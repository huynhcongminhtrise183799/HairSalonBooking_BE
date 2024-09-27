
package com.example.hairSalonBooking.controller;


import com.example.hairSalonBooking.entity.Account;
import com.example.hairSalonBooking.model.request.IntrospectRequest;
import com.example.hairSalonBooking.model.response.AccountResponse;
import com.example.hairSalonBooking.model.request.LoginRequest;
import com.example.hairSalonBooking.model.request.RegisterRequest;
import com.example.hairSalonBooking.model.response.AuthenticationResponse;
import com.example.hairSalonBooking.model.response.IntrospectResponse;
import com.example.hairSalonBooking.repository.CustomerRepository;
import com.example.hairSalonBooking.service.AuthenticationService;
import com.example.hairSalonBooking.service.CustomerService;
import com.example.hairSalonBooking.service.TokenService;
import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.models.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.processing.Filer;
import java.security.Principal;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/api") // giảm bớt đường dẫn
@SecurityRequirement(name="bearerAuth")
public class AuthenticationAPI {


    // DI :Dependency Injection


    @Autowired // mình sử dụng để gọi thằng service phục vụ
    AuthenticationService authenticationService;

    @Autowired
    CustomerService customerService;

    @Autowired
    CustomerRepository customerRepository;

    @PostMapping("/logingoogle")
    public ResponseEntity<AuthenticationResponse> outboundAuthenticate(@RequestParam("code") String code) throws JOSEException {
        var result = authenticationService.outboundAuthenticate(code);
        return ResponseEntity.ok(result);

    }

    @PostMapping("/register")
    public ResponseEntity register(@Valid @RequestBody RegisterRequest registerRequest) {
        AccountResponse newAccount = authenticationService.register(registerRequest);
        return ResponseEntity.ok(newAccount);
    }

    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody LoginRequest loginRequest) {
        AccountResponse newAccount = authenticationService.login(loginRequest);
        return ResponseEntity.ok(newAccount);
    }

//    @GetMapping("/profile")
//    public ResponseEntity getAllAccount() {
//        List<Account> accounts = authenticationService.getAllAccount();
//        return ResponseEntity.ok(accounts);
//    }
//    @PostMapping("/introspect")
//    public ResponseEntity introspect(@Valid @RequestBody IntrospectRequest Request) throws ParseException, JOSEException {
//        IntrospectResponse introspectResponse = authenticationService.introspect(Request);
//        return ResponseEntity.ok(introspectResponse);
//    }


    @GetMapping("/profile")
    public ResponseEntity<?> getAllInfo(@RequestParam Long id){
        Account account = customerService.getAccountById(id);
        return ResponseEntity.ok(account);

    }





}
