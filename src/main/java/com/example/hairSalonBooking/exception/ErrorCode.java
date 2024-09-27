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
    PASSWORD_SIZE_INVALID(1005, "password must be at least 6 character"),
    INVALID_EMAIL(1006,"Invalid email"),
    UNCATEGORIZED_EXCEPTION(1007,"Password or Username invalid"),
    ACCOUNT_Not_Found_Exception(1008,"Account not found"),
    INVALID_PHONE(1009,"Invalid phone number"),
    INVALID_KEY (1010, "Invalid message key"),
    USERNAME_EXISTED(1011,"Username already existed"),
    EMAIL_EXISTED(1012,"Email already existed"),
    Phone_EXISTED(1013,"Phone already existed"),
    INVALID_USERNAME(1014,"Username can not be blank"),
    INVALID_DOB(1015,"Date of birth is not valid"),
    SERVICE_NO_BLANK(1016, "SalonService name can not be blank"),
    PRICE_NO_BLANK(1017, "Price can not be blank"),
    ;
    int code;
    String message;
}