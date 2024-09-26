package com.example.hairSalonBooking.model.request;


import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class StylistRequest {


    @NotBlank(message = "INVALID_USERNAME")
    String username;

    @Email(message = "INVALID_EMAIL")
    String email;

    String fullname;

    @Pattern(regexp = "(84|0[35789])\\d{8}\\b",message = "INVALID_PHONE")
    String phone;

    String gender;

    Long salonid;

    Long levelid;

}
