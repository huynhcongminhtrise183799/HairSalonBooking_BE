package com.example.hairSalonBooking.controller;

import com.example.hairSalonBooking.model.request.UpdateCustomerRequest;
import com.example.hairSalonBooking.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
@CrossOrigin("*")
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @PutMapping("/{AccountId}")
    public ResponseEntity updateCustomer(@PathVariable long AccountId, @Valid @RequestBody UpdateCustomerRequest request){
        return ResponseEntity.ok(customerService.updateCustomer(request,AccountId));
    }
}
