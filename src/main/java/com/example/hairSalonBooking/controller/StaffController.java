package com.example.hairSalonBooking.controller;

import com.example.hairSalonBooking.model.request.CreateStaffRequest;
import com.example.hairSalonBooking.model.request.UpdateStaffRequest;
import com.example.hairSalonBooking.model.response.ApiResponse;
import com.example.hairSalonBooking.model.response.StaffResponse;
import com.example.hairSalonBooking.service.StaffService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
@CrossOrigin("http://localhost:3000/")
@SecurityRequirement(name = "api")
public class StaffController {
    @Autowired
    private StaffService staffService;
    @PostMapping("staff")
    public ApiResponse<StaffResponse> createStaff(@Valid @RequestBody CreateStaffRequest request){
        ApiResponse response = new ApiResponse<>();
        response.setMessage("Created new staff");
        response.setResult(staffService.createStaff(request));
        return response;
    }

    @GetMapping("/staffs")
    public ApiResponse<StaffResponse> getAllStaff(){
        ApiResponse response = new ApiResponse<>();
        response.setResult(staffService.getAllStaffs());
        return response;
    }

    @PutMapping("/staff/{id}")
    public ApiResponse<StaffResponse> updateStaff(@PathVariable long id, @RequestBody UpdateStaffRequest request){
        ApiResponse response = new ApiResponse<>();
        response.setResult(staffService.updateStaff(request,id));
        return response;
    }

    @DeleteMapping("/staff/{id}")
    public ApiResponse<StaffResponse> deleteStaff(@PathVariable long id){
        ApiResponse response = new ApiResponse<>();
        response.setResult(staffService.deleteStaff(id));
        return response;
    }
}
