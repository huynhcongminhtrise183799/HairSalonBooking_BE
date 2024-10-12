package com.example.hairSalonBooking.controller;

import com.example.hairSalonBooking.model.request.CreateFeedBackRequest;
import com.example.hairSalonBooking.model.response.ApiResponse;
import com.example.hairSalonBooking.model.response.FeedbackResponse;
import com.example.hairSalonBooking.service.FeedbackService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin("http://localhost:3000/")
@SecurityRequirement(name = "api")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping("/feedback")
    public ApiResponse<FeedbackResponse> createFeedback(@Valid @RequestBody CreateFeedBackRequest request){
        ApiResponse response = new ApiResponse<>();
        response.setResult(feedbackService.createFeedback(request));
        return response;
    }
}
