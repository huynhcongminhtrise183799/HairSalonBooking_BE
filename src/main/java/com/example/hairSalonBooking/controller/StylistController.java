package com.example.hairSalonBooking.controller;


import com.example.hairSalonBooking.model.request.SalaryRecordRequest;
import com.example.hairSalonBooking.model.request.StylistRequest;
import com.example.hairSalonBooking.model.request.UpdateStylistRequest;

import com.example.hairSalonBooking.model.response.*;

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

    @GetMapping("/read")
    public ApiResponse<StylistResponse> getAllStylist() {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(stylistService.getAllStylist());
        return apiResponse;
    }
    @GetMapping("/read/{accountId}")
    public ApiResponse<StylistResponse> getSpecificStylist(@PathVariable long accountId) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(stylistService.getSpecificStylist(accountId));
        return apiResponse;
    }
    @GetMapping("/salon/{salonId}")
    public ApiResponse<List<StylistForCreateSchedule>> getStylistsBySalon(@PathVariable long salonId) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(stylistService.getStylistsBySalon(salonId));
        return apiResponse;
    }
    @GetMapping("/page/{salonId}")
    public ApiResponse<StylistPageResponse> getAllAccountStylist(@RequestParam int page, @RequestParam int size, @PathVariable long salonId) {
    ApiResponse response = new ApiResponse<>();
    response.setResult(stylistService.getAllAccountStylist(page, size,salonId));
    return response;
    }
    
    @GetMapping("/status")
    public ResponseEntity getStylistByStatus() {        
        List<StylistResponse> StylistStatus = stylistService.getStylistByStatus();
        return ResponseEntity.ok(StylistStatus);
    }

    
    @PutMapping("{accountid}") // Đảm bảo có dấu "/"
    public ApiResponse<StylistResponse> updateStylist(
            @PathVariable long accountid, // Sửa tên tham số thành stylistId
            @Valid @RequestBody UpdateStylistRequest stylistRequest) { // Sử dụng UpdateStylistRequest
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

    // lấy service của stylist đó
    @GetMapping("/service/{accountId}")
    public ApiResponse<List<StylistServiceResponse>> getAllServiceByStylistId(@RequestParam long accountid) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(stylistService.getAllServiceByStylistId(accountid));
        return apiResponse;
    }

    @GetMapping("/salaries/{salonId}/{yearAndMonth}")
    public ApiResponse<SalaryResponse> getSalaries(
            @PathVariable Long salonId,
            @PathVariable String yearAndMonth) {
        ApiResponse response = new ApiResponse<>();
        response.setResult(stylistService.calculateTotalSalary(salonId, yearAndMonth));
        return response;
    }
    @GetMapping("/salaries/{salonId}/{yearAndMonth}/{stylistId}")
    public ApiResponse<SalaryResponse> getSalaries(
            @PathVariable Long salonId,
            @PathVariable String yearAndMonth,
            @PathVariable Long stylistId) { // Make stylistId optional
        ApiResponse<SalaryResponse> response = new ApiResponse<>();
        response.setResult(stylistService.calculateTotalSalaryByOneStylist(salonId, yearAndMonth, stylistId));
        return response;
    }
    @PostMapping("/salaryRecords/save")
    public ApiResponse<SalaryRecordRequest> saveSalaryRecords(@RequestBody List<SalaryRecordRequest> salaryRecordRequests) {
        ApiResponse response = new ApiResponse<>();
        response.setResult(stylistService.saveSalaryRecords(salaryRecordRequests));
        return response;
    }
@GetMapping("/salaryRecords/{salonId}/{yearAndMonth}")
    public ApiResponse<SalaryRecordRequest> getSalaryRecords(
            @PathVariable Long salonId,
            @PathVariable String yearAndMonth) {
        ApiResponse response = new ApiResponse<>();
        response.setResult(stylistService.getSalaryRecords(salonId, yearAndMonth));
        return response;
    }
}
