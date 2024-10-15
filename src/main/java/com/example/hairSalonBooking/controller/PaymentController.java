package com.example.hairSalonBooking.controller;


import com.example.hairSalonBooking.entity.Account;
import com.example.hairSalonBooking.entity.Booking;
import com.example.hairSalonBooking.entity.Payment;
import com.example.hairSalonBooking.entity.Transactions;
import com.example.hairSalonBooking.enums.BookingStatus;
import com.example.hairSalonBooking.enums.Role;
import com.example.hairSalonBooking.exception.AppException;
import com.example.hairSalonBooking.exception.ErrorCode;
import com.example.hairSalonBooking.model.response.ResponseObject;
import com.example.hairSalonBooking.repository.AccountRepository;
import com.example.hairSalonBooking.repository.BookingRepository;
import com.example.hairSalonBooking.repository.PaymentRepository;
import com.example.hairSalonBooking.repository.TransactionsRepository;
import com.example.hairSalonBooking.service.PaymentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.hairSalonBooking.config.VnpayConfig;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin("http://localhost:3000/")
//@CrossOrigin("http://localhost:8080/payment/response")
@SecurityRequirement(name = "api")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private TransactionsRepository transactionsRepository;
    @Autowired
    private AccountRepository accountRepository;
    @GetMapping("/Pay/{bookingId}")
    public String getPay(@PathVariable Long bookingId, HttpServletRequest req ) throws UnsupportedEncodingException {

        return paymentService.generatePaymentUrl(bookingId, req);
    }

    @GetMapping("/payment/response")
    public ResponseEntity<String> checkPaymentSuccess(@RequestParam String vnp_BankCode,
                                                      @RequestParam String vnp_CardType,
                                                      @RequestParam String vnp_ResponseCode,
                                                      @RequestParam String vnp_TxnRef) {

        return paymentService.checkPaymentSuccess(vnp_BankCode, vnp_CardType, vnp_ResponseCode, vnp_TxnRef);
    }

}
