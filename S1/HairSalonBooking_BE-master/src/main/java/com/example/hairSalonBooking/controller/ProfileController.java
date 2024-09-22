package com.example.hairSalonBooking.controller;

import com.example.hairSalonBooking.entity.Account;
import com.example.hairSalonBooking.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ProfileController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/profile")
    public ResponseEntity<?> viewProfile(@RequestParam long accountId) {
        try {
            Account account = accountService.getAccountById(accountId);

            if (account != null) {
                return ResponseEntity.ok(account);
            } else {
                // Trả về 404 nếu không tìm thấy tài khoản
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
            }
        } catch (Exception e) {
            // In ra lỗi và trả về phản hồi với mã 500
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: " + e.getMessage());
        }
    }
}
