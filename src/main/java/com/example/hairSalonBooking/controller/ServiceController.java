package com.example.hairSalonBooking.controller;

import com.example.hairSalonBooking.entity.SalonService;
import com.example.hairSalonBooking.model.request.CreateServiceRequest;
import com.example.hairSalonBooking.model.request.ServiceUpdateRequest;
import com.example.hairSalonBooking.model.response.ApiResponse;
import com.example.hairSalonBooking.model.response.ServiceResponse;
import com.example.hairSalonBooking.service.HairSalonServiceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/service")
@CrossOrigin("http://localhost:3000/")
public class ServiceController {

    @Autowired
    private HairSalonServiceService  hairSalonServiceService;

    @PostMapping
    ApiResponse<SalonService> createService(
            @RequestParam("image") MultipartFile file,
            @Valid @RequestPart("request") CreateServiceRequest Request) {
        ApiResponse<SalonService> apiResponse = new ApiResponse<>();
        apiResponse.setResult(hairSalonServiceService.createService(file,Request));
        return apiResponse;
    }
    @GetMapping
    List<SalonService> getAllServices() {
        return hairSalonServiceService.getAllServices();
    }
    @GetMapping("/searchById")
    Optional<SalonService> SearchServiceById(@RequestParam long serviceId) {
        return hairSalonServiceService.searchServiceId(serviceId);
    }
    @GetMapping("/searchByName")
    List<SalonService> SearchServiceName(@RequestParam String serviceName) {
        return hairSalonServiceService.searchServiceByName(serviceName);
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
