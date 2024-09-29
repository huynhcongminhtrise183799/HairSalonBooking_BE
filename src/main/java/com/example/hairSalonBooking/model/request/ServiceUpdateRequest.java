package com.example.hairSalonBooking.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceUpdateRequest {
    @NotBlank(message = "SERVICE_NO_BLANK")
    String serviceName;
    @NotNull(message = "PRICE_NO_BLANK")
    int price;
    String description;
    LocalTime duration;
}
