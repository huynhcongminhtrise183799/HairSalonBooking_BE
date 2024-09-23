package com.example.hairSalonBooking.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
    EMAIL_NOT_FOUND(1001,"Email is not correct"),
    INVALID_OTP(1002,"Invalid OTP"),
    PASSWORD_NOT_MATCH(1003,"Password not match"),
    OTP_HAS_EXPIRED(1004,"OTP has expired"),
    ;
    int code;
    String message;
}
