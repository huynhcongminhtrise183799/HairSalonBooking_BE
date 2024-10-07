package com.example.hairSalonBooking.controller;


import com.example.hairSalonBooking.model.request.StylistRequest;
import com.example.hairSalonBooking.model.response.ApiResponse;
import com.example.hairSalonBooking.model.response.StylistResponse;
import com.example.hairSalonBooking.service.StylistService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stylist")
@CrossOrigin(origins = "http://localhost:3000")
@SecurityRequirement(name = "api")
public class StylistController {

    @Autowired
    StylistService stylistService;


    @PostMapping("/create")
    public ApiResponse<StylistResponse> createStylist(@Valid @RequestBody StylistRequest stylistRequest) {
        ApiResponse response = new ApiResponse<>();
        response.setResult(stylistService.create(stylistRequest));
        return response;
    }
    @GetMapping("/status")
    public ResponseEntity getStylistByStatus() {
        List<StylistResponse> StylistStatus = stylistService.getStylistByStatus();
        return ResponseEntity.ok(StylistStatus);
    }

    @GetMapping("/read")
    List<StylistResponse> getAllStylist() {
      return stylistService.getAllStylist();
    }

    @PutMapping("{accountid}") // Đảm bảo có dấu "/"
    public ApiResponse<StylistResponse> updateStylist(
            @PathVariable long accountid, // Sửa tên tham số thành stylistId
            @Valid @RequestBody StylistRequest stylistRequest) { // Sử dụng StylistRequest
        ApiResponse response = new ApiResponse<>();
        response.setResult(stylistService.updateStylist(accountid, stylistRequest));
        return response; // Trả về StylistResponse
    }

    @DeleteMapping("{accountid}")
    public ApiResponse<StylistResponse> deleteStylist(@PathVariable long accountid){ // Sửa tên tham số thành stylistId)
        ApiResponse response = new ApiResponse<>();
        response.setResult(stylistService.deleteStylist(accountid));
        return response;// Trả về StylistResponse
    }




}
