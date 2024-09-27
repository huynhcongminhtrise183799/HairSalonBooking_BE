package com.example.hairSalonBooking.exception;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public enum ErrorCode {

    EMAIL_NOT_FOUND(1001,"Email is not correct"),
    INVALID_OTP(1002,"Invalid OTP"),
    PASSWORD_NOT_MATCH(1003,"Password not match"),
    OTP_HAS_EXPIRED(1004,"OTP has expired"),
    REGISTER_EXITS(1005,"Register exits");


    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
