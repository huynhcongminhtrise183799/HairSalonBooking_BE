package com.example.hairSalonBooking.controller;


import com.example.hairSalonBooking.entity.Account;
import com.example.hairSalonBooking.model.request.StylistRequest;
import com.example.hairSalonBooking.model.response.StylistResponse;
import com.example.hairSalonBooking.service.StylistService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stylist")
@CrossOrigin(origins = "http://localhost:3000")
public class StylistController {

    @Autowired
    StylistService stylistService;


    @PostMapping("/create")
    public ResponseEntity<StylistResponse> createStylist(@Valid @RequestBody StylistRequest stylistRequest) {
        StylistResponse newStylist = stylistService.create(stylistRequest);
        return ResponseEntity.ok(newStylist);
    }

    @GetMapping("/read")
    public ResponseEntity getAllStylist() {
        List<StylistResponse> Stylists = stylistService.getAllStylist();
        return ResponseEntity.ok(Stylists);
    }

    @GetMapping("/status")
    public ResponseEntity getStylistByStatus() {
        List<StylistResponse> StylistStatus = stylistService.getStylistByStatus();
        return ResponseEntity.ok(StylistStatus);
    }

    @PutMapping("{accountid}") // Đảm bảo có dấu "/"
    public ResponseEntity<StylistResponse> updateStylist(
            @PathVariable long accountid, // Sửa tên tham số thành stylistId
            @Valid @RequestBody StylistRequest stylistRequest) { // Sử dụng StylistRequest

        StylistResponse updatedStylist = stylistService.updateStylist(accountid, stylistRequest); // Gọi service
        return ResponseEntity.ok(updatedStylist); // Trả về StylistResponse
    }

    @DeleteMapping("{accountid}")
    public ResponseEntity<StylistResponse> deleteStylist(@PathVariable long accountid){ // Sửa tên tham số thành stylistId)
        StylistResponse deleteStylist = stylistService.deleteStylist(accountid); // Gọi service
        return ResponseEntity.ok(deleteStylist); // Trả về StylistResponse
    }
}
