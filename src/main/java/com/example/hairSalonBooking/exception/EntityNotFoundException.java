package com.example.hairSalonBooking.exception;

public class EntityNotFoundException extends RuntimeException {

    // mình tạo ra báo lô của riêng mình
    public EntityNotFoundException(String message) {
        super(message);
    }

}
