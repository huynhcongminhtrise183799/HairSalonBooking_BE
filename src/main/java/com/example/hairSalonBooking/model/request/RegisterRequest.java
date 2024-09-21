package com.example.hairSalonBooking.model.request;


import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Username can not be blank!")
    @Column(unique = true)
    String Username;

    @Email(message = "Invalid email")
    @NotBlank(message = "Email can not be blank!")
    String email;

    @Pattern(regexp = "(84|0[3|5|7|8|9])+(\\d{8})\\b", message = "Invalid phone, you need input correct")
    @NotBlank(message = "Phone can not be blank!")
    String phone;

    @Size(min = 6, message = "Password must be at least 6 character!")
    @NotBlank(message = "Password can not be blank!")
    String password;
    String ConfirmPassword;
}

