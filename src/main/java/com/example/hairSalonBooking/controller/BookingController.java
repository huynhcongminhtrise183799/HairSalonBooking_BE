package com.example.hairSalonBooking.controller;

import com.example.hairSalonBooking.entity.Booking;
import com.example.hairSalonBooking.entity.Shift;
import com.example.hairSalonBooking.entity.Slot;
import com.example.hairSalonBooking.model.request.BookingRequest;
import com.example.hairSalonBooking.model.request.BookingSlots;
import com.example.hairSalonBooking.model.request.BookingStylits;
import com.example.hairSalonBooking.model.response.ApiResponse;
import com.example.hairSalonBooking.model.response.BookingResponse;
import com.example.hairSalonBooking.model.response.ShiftResponse;
import com.example.hairSalonBooking.model.response.StylistForBooking;
import com.example.hairSalonBooking.service.BookingService;
import com.example.hairSalonBooking.service.HairSalonServiceService;
import com.example.hairSalonBooking.service.StylistService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/booking/stylists")
    public ApiResponse<Set<StylistForBooking>> getListService(@RequestBody BookingStylits bookingStylits){
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(bookingService.getStylistForBooking(bookingStylits));
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
    // lay ra theo trang thai
    @GetMapping("/customer/{accountId}/pending")
    public ApiResponse<List<Booking>> getPendingBookings(@PathVariable Long accountId){
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(bookingService.getBookingByStatusPendingByCustomer(accountId));
        return apiResponse;
    }
//    @GetMapping("/customer/{accountId}/in-progress")
//    public ApiResponse<List<Booking>> getInProcessBookings(@PathVariable Long accountId){
//        ApiResponse apiResponse = new ApiResponse<>();
//        apiResponse.setResult(bookingService.getBookingByStatusIN_PROGRESSByCustomer(accountId));
//        return apiResponse;
//    }
    @GetMapping("/customer/{accountId}/completed")
    public ApiResponse<List<Booking>> getCompleteBookings(@PathVariable Long accountId){
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(bookingService.getBookingByStatusCompletedByCustomer(accountId));
        return apiResponse;
    }
    // controller checkin
//    @PutMapping("/{bookingId}/checkin")
//    public ApiResponse<Booking> checkIn(@PathVariable Long bookingId) {
//         ApiResponse apiResponse = new ApiResponse<>();
//         apiResponse.setResult(bookingService.checkIn(bookingId));
//         return apiResponse;
//    }

}
