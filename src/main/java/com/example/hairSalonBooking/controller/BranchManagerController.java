package com.example.hairSalonBooking.controller;

import com.example.hairSalonBooking.model.request.CreateManagerRequest;
import com.example.hairSalonBooking.model.request.UpdateManagerRequest;
import com.example.hairSalonBooking.model.response.ApiResponse;
import com.example.hairSalonBooking.model.response.BookingResponse;
import com.example.hairSalonBooking.model.response.ManagerResponse;
import com.example.hairSalonBooking.service.BranchManagerService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/")
@CrossOrigin("http://localhost:3000/")
@SecurityRequirement(name = "api")
public class BranchManagerController {
    @Autowired
    private BranchManagerService branchManagerService;

    @PostMapping("/manager")
    public ApiResponse<ManagerResponse> createManager(@Valid @RequestBody CreateManagerRequest request){
        ApiResponse response = new ApiResponse<>();
        response.setMessage("Created new manager");
        response.setResult(branchManagerService.createManager(request));
        return response;
    }

    @GetMapping("/managers")
    public ApiResponse<ManagerResponse> getAllManagers(){
        ApiResponse response = new ApiResponse<>();
        response.setResult(branchManagerService.getAllManagers());
        return response;
    }

    @PutMapping("/manager/{id}")
    public ApiResponse<ManagerResponse> updateManager(@PathVariable long id, @RequestBody UpdateManagerRequest request){
        ApiResponse response = new ApiResponse<>();
        response.setResult(branchManagerService.updateManager(id,request));
        return response;
    }
    @DeleteMapping("/manager/{id}")
    public ApiResponse<ManagerResponse> deleteManager(@PathVariable long id){
        ApiResponse response = new ApiResponse<>();
        response.setResult(branchManagerService.deleteManager(id));
        return response;
    }

    // Hàm này để lấy tất cả booking của stylist theo Chi nhánh bạn chọn
    @GetMapping("/managers/stylist")
    public ApiResponse<List<BookingResponse>> getAllBookingsForStylistInBranch(@RequestParam Long branchId) {
        ApiResponse<List<BookingResponse>> response = new ApiResponse<>();
        response.setResult(branchManagerService.getAllBookingsForStylistsInBranch(branchId));
        return response;
    }
}
