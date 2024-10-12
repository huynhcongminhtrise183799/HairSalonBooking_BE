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
    @PutMapping("/booking/{bookingId}")
    public ApiResponse<Booking> updateBooking(@PathVariable long bookingId,@RequestBody BookingRequest request){
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(bookingService.updateBooking(bookingId,request));
        return apiResponse;
    }
    @DeleteMapping("/booking/{bookingId}")
    public ApiResponse createBooking(@PathVariable Long bookingId){
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(bookingService.deleteBooking(bookingId));
        return apiResponse;
    }
    // lay ra theo trang thai
    @GetMapping("/customer/{accountId}/pending")
    public ApiResponse<List<Booking>> getPendingBookings(@PathVariable Long accountId){
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(bookingService.getBookingByStatusPendingByCustomer(accountId));
        return apiResponse;
    }

    @GetMapping("/customer/{accountId}/completed")
    public ApiResponse<List<Booking>> getCompleteBookings(@PathVariable Long accountId){
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(bookingService.getBookingByStatusCompletedByCustomer(accountId));
        return apiResponse;
    }
    // controller checkin
    @PutMapping("/{bookingId}/checkin")
    public ApiResponse<String> checkIn(@PathVariable Long bookingId) {
         ApiResponse apiResponse = new ApiResponse<>();
         apiResponse.setResult(bookingService.checkIn(bookingId));
         return apiResponse;
    }
    @PutMapping("/{Transid}/checkout")
    public ApiResponse<String> checkIn(@PathVariable String Transid) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(bookingService.checkout(Transid));
        return apiResponse;
    }
    @PostMapping("/{bookingId}/finish")
    public ApiResponse<Double> finishBooking(@PathVariable Long bookingId) {
        ApiResponse<Double> apiResponse = new ApiResponse<>();
        double totalAmount = bookingService.finishedService(bookingId);
        apiResponse.setResult(totalAmount);
        return apiResponse;
    }
    @GetMapping("/bookings/stylist/{date}/{accountId}")
    public ApiResponse<List<BookingResponse>> getTodayBookingsForStylist(@PathVariable Long accountId, @PathVariable String date) {
        ApiResponse apiResponse = new ApiResponse<>();
        LocalDate localDate = LocalDate.parse(date); // chắc rằng form đúng yyyy-mm-dd
        apiResponse.setResult(stylistService.getBookingsForStylistOnDate(accountId, localDate));
        return apiResponse;
    }


}
