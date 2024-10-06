package com.example.hairSalonBooking.controller;

import com.example.hairSalonBooking.entity.SalonService;
import com.example.hairSalonBooking.model.request.CreateServiceRequest;
import com.example.hairSalonBooking.model.request.BookingStylits;
import com.example.hairSalonBooking.model.request.SearchServiceNameRequest;
import com.example.hairSalonBooking.model.request.ServiceUpdateRequest;
import com.example.hairSalonBooking.model.response.ApiResponse;
import com.example.hairSalonBooking.model.response.ServiceResponse;
import com.example.hairSalonBooking.service.HairSalonServiceService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/service")
@CrossOrigin("http://localhost:3000/")
@SecurityRequirement(name = "api")
public class ServiceController {

    @Autowired
    private HairSalonServiceService  hairSalonServiceService;

    @PostMapping
    ApiResponse<SalonService> createService(@Valid @RequestBody CreateServiceRequest Request) {
        ApiResponse<SalonService> apiResponse = new ApiResponse<>();
        apiResponse.setResult(hairSalonServiceService.createService(Request));
        return apiResponse;
    }
    @GetMapping
    ApiResponse<List<ServiceResponse>> getAllServices() {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(hairSalonServiceService.getAllServices());
        return apiResponse;
    }
    @GetMapping("/searchById")
    Optional<SalonService> SearchServiceById(@RequestParam long serviceId) {
        return hairSalonServiceService.searchServiceId(serviceId);
    }
    @GetMapping("/searchByName")
    ApiResponse<List<ServiceResponse>> SearchServiceName(@RequestBody SearchServiceNameRequest serviceName) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(hairSalonServiceService.searchServiceByName(serviceName));

        return apiResponse;
    }



    @DeleteMapping("/delete")
    String deleteUser(@RequestParam Long serviceId) {
        hairSalonServiceService.deleteService(serviceId);
        return "service deleted";
    }
    @PutMapping("/update")
    public ServiceResponse updateService(@RequestParam long serviceId, @RequestBody ServiceUpdateRequest request) {
        return hairSalonServiceService.updateService(serviceId, request);
    }

}
