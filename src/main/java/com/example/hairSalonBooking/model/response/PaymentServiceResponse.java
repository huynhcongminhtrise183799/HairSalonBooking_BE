package com.example.hairSalonBooking.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class PaymentServiceResponse {
    String serviceName;
    int price;

    public PaymentServiceResponse(String serviceName, int price) {
        this.serviceName = serviceName;
        this.price = price;
    }
}
