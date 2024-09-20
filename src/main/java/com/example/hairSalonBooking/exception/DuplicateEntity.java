package com.example.hairSalonBooking.exception;

public class DuplicateEntity extends RuntimeException {

    // mình tạo ra báo lô của riêng mình
    public DuplicateEntity(String message) {
        super(message);
    }

}
