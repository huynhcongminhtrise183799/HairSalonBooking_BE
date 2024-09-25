
package com.example.hairSalonBooking.controller;


import com.example.hairSalonBooking.entity.Account;
import com.example.hairSalonBooking.model.AccountResponse;
import com.example.hairSalonBooking.model.LoginRequest;
import com.example.hairSalonBooking.model.RegisterRequest;
import com.example.hairSalonBooking.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

        import java.util.List;

@RestController
@RequestMapping("/api") // giảm bớt đường dẫn
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
        AccountResponse newAccount = authenticationService.login(loginRequest);
        return ResponseEntity.ok(newAccount);
    }

    @GetMapping("/account")
    public ResponseEntity getAllAccount() {
        List<Account> accounts = authenticationService.getAllAccount();
        return ResponseEntity.ok(accounts);
    }



}
