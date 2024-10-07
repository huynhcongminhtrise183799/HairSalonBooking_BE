package com.example.hairSalonBooking.controller;

import com.cloudinary.Api;
import com.example.hairSalonBooking.entity.Booking;
import com.example.hairSalonBooking.entity.Slot;
import com.example.hairSalonBooking.model.request.BookingRequest;
import com.example.hairSalonBooking.model.request.BookingSlots;
import com.example.hairSalonBooking.model.request.BookingStylits;
import com.example.hairSalonBooking.model.response.ApiResponse;
import com.example.hairSalonBooking.model.response.BookingResponse;
import com.example.hairSalonBooking.model.response.StylistForBooking;
import com.example.hairSalonBooking.repository.BookingRepository;
import com.example.hairSalonBooking.service.BookingService;
import com.example.hairSalonBooking.service.HairSalonServiceService;
import com.example.hairSalonBooking.service.StylistService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
@CrossOrigin("http://localhost:3000/")
@SecurityRequirement(name = "api")
public class BookingController {
    @Autowired
    private StylistService stylistService;
    @Autowired
    private HairSalonServiceService hairSalonServiceService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private BookingRepository bookingRepository;

    @PostMapping("/booking/stylists")
    public ApiResponse<Set<StylistForBooking>> getListService(@RequestBody BookingStylits bookingStylits){
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(bookingService.getListService(bookingStylits));
        return apiResponse;
    }
    @PostMapping("/booking/slots")
    public ApiResponse<List<Slot>> getListSlots(@RequestBody BookingSlots bookingSlots){
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(bookingService.getListSlot(bookingSlots));
        return apiResponse;
    }

    @PostMapping("/booking")
    public ApiResponse<Booking> createBooking(@RequestBody BookingRequest request){
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(bookingService.createNewBooking(request));
        return apiResponse;
    }


    @GetMapping("/bookings/today/{date}/{accountId}")
    public ApiResponse<List<BookingResponse>> getTodayBookingsForStylist(@PathVariable Long accountId, @PathVariable String date) {
        ApiResponse apiResponse = new ApiResponse<>();
        LocalDate localDate = LocalDate.parse(date); // chắc rằng form đúng yyyy-mm-dd
        apiResponse.setResult(bookingService.getBookingsForStylistOnDate(accountId, localDate));
        return apiResponse;
    }

}
