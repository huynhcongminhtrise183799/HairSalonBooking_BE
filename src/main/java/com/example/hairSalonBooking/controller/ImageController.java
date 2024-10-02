package com.example.hairSalonBooking.controller;


import com.example.hairSalonBooking.entity.Customer;
import com.example.hairSalonBooking.service.ImageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.hairSalonBooking.utils.ImageUtils;

import java.io.IOException;

@RestController
@RequestMapping("/api/image")
public class ImageController {
    @Autowired
    private ImageService imageService;

//    @PostMapping("/upload")
//    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
//        String uploadImage = imageService.uploadImage(file);
//        return ResponseEntity.status(HttpStatus.OK).body(uploadImage);
//
//    }
//@PostMapping("/upload")
//public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile file,
//                                          @RequestParam("customer") String customerJson) throws IOException {
//    // Chuyển đổi chuỗi JSON thành đối tượng Customer
//    ObjectMapper objectMapper = new ObjectMapper();
//    Customer customer = objectMapper.readValue(customerJson, Customer.class);
//
//    String uploadImage = imageService.uploadImage(file, customer);
//    return ResponseEntity.status(HttpStatus.OK).body(uploadImage);
//}

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile file,
                                              @RequestBody Customer customer) throws IOException {
        // Chuyển đổi chuỗi JSON thành đối tượng Customer
//        ObjectMapper objectMapper = new ObjectMapper();
//        Customer customer = objectMapper.readValue(customerJson, Customer.class);

        String uploadImage = imageService.uploadImage(file, customer);
        return ResponseEntity.status(HttpStatus.OK).body(uploadImage);
    }
    @GetMapping("/{fileName}")
    public ResponseEntity<?> downloadImage(@PathVariable String fileName) throws IOException {
        byte[] imageData= imageService.downloadImage(fileName);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("image/png"))
                .body(imageData);
    }
}
