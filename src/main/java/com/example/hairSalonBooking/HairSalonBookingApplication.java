package com.example.hairSalonBooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class HairSalonBookingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HairSalonBookingApplication.class, args);
	}

}
