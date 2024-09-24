package com.example.hairSalonBooking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice// class de handler nhung exception
public class ValidationHandler {
    // dinh nghia cho no chay moi khi gap 1 cai exception nao do
    //MethodArgumentNotValidException.class: la loi khi nhap sai
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)// dau vao sai, front-end check lai
    public ResponseEntity handleValidation(MethodArgumentNotValidException exception){
        String message = "";

        // cu moi thuoc tinh loi => xu li

        for(FieldError fieldError : exception.getBindingResult().getFieldErrors()){
            // fielError: Name, studentcode,..
            System.out.println(fieldError);
            message += fieldError.getField() + ": " + fieldError.getDefaultMessage();

        }
        return new ResponseEntity(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleValidation(Exception exception){
        return new ResponseEntity(exception.getMessage(),HttpStatus.BAD_REQUEST);
    }
}
