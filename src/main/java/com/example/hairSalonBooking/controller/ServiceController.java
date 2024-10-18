package com.example.hairSalonBooking.controller;



import com.example.hairSalonBooking.model.request.CreateServiceRequest;
import com.example.hairSalonBooking.model.request.SearchServiceNameRequest;
import com.example.hairSalonBooking.model.request.ServiceUpdateRequest;
import com.example.hairSalonBooking.model.response.ApiResponse;
import com.example.hairSalonBooking.model.response.ServiceResponse;
import com.example.hairSalonBooking.model.response.*;
import com.example.hairSalonBooking.service.HairSalonServiceService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.example.hairSalonBooking.repository.ServiceRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;

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

    @Autowired
    private ServiceRepository serviceRepository;


    @PostMapping
    ApiResponse<ServiceResponse> createService(@Valid @RequestBody CreateServiceRequest Request) {
        ApiResponse<ServiceResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(hairSalonServiceService.createService(Request));
        return apiResponse;
    }



    @GetMapping
    ApiResponse<List<ServiceResponse>> getAllServices() {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(hairSalonServiceService.getAllServicesActive());
        return apiResponse;
    }


    @GetMapping("/{serviceId}")
    ApiResponse<ServiceResponse> SearchServiceById(@PathVariable long serviceId) {
        ApiResponse response = new ApiResponse<>();
        response.setResult(hairSalonServiceService.searchServiceId(serviceId));
        return response ;
    }
    @PostMapping("/searchByName")
    ApiResponse<List<ServiceResponse>> SearchServiceName(@RequestBody SearchServiceNameRequest serviceName) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(hairSalonServiceService.searchServiceByName(serviceName));
        return apiResponse;
    }


    @DeleteMapping("/delete/{serviceId}")
    String deleteUser(@PathVariable long serviceId) {
        hairSalonServiceService.deleteService(serviceId);
        return "service deleted";
    }
    @PutMapping("/update/{serviceId}")
    public ServiceResponse updateService(@PathVariable long serviceId, @RequestBody ServiceUpdateRequest request) {
        return hairSalonServiceService.updateService(serviceId, request);
    }


    @GetMapping("/page")
    public ApiResponse<ServicePageResponse> getAllServicePage(@RequestParam int page, @RequestParam int size) {
        ApiResponse response = new ApiResponse<>();
        response.setResult(hairSalonServiceService.getAllServicePage(page, size));
        return response;

    }
    @GetMapping("/all")
    public ResponseEntity getAllService() {
        List<ServiceResponse> services = hairSalonServiceService.getAllServices();
        return ResponseEntity.ok(services);
    }

}
