package com.example.hairSalonBooking.controller;

import com.example.hairSalonBooking.model.request.UpdateCustomerRequest;
import com.example.hairSalonBooking.model.response.ApiResponse;
import com.example.hairSalonBooking.model.response.ProfileResponse;
import com.example.hairSalonBooking.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
@CrossOrigin("http://localhost:3000/")
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @PutMapping("/{AccountId}")
    public ApiResponse<UpdateCustomerRequest> updateCustomer(@PathVariable long AccountId, @Valid @RequestBody UpdateCustomerRequest request){
        ApiResponse response = new ApiResponse();
        response.setResult(customerService.updateCustomer(request,AccountId));
        response.setMessage("Update successfully");

        return response;
    }

    @GetMapping("/profile")
    public ApiResponse<ProfileResponse> getProfile(){
        ApiResponse response = new ApiResponse();
        response.setResult(customerService.getProfile());
        return response;

    }
}
