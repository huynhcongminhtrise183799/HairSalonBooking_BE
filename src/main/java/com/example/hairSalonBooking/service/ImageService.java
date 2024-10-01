package com.example.hairSalonBooking.service;

import com.example.hairSalonBooking.entity.Customer;
import com.example.hairSalonBooking.entity.Image;
import com.example.hairSalonBooking.exception.AppException;
import com.example.hairSalonBooking.exception.ErrorCode;
import com.example.hairSalonBooking.repository.ImageRepository;
import com.example.hairSalonBooking.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class ImageService {
    @Autowired
    private ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

//    public String uploadImage(MultipartFile file) throws IOException {
//        String originalFilename = file.getOriginalFilename();
//
//        // Kiểm tra nếu tên hình ảnh đã tồn tại trong database
//        if (imageRepository.existsByName(originalFilename)) {
//            // Ném ngoại lệ nếu tên hình ảnh đã tồn tại
//            throw new AppException(ErrorCode.NAME_IMAGE_EXISTED);
//        }
//            Image imageData = imageRepository.save(
//                    Image.builder()
//                            .name(file.getOriginalFilename())
//                            .type(file.getContentType())
//                            .imageData(ImageUtils.compressImage(file.getBytes()))
//                            .build()
//            );
//                return "File ảnh update thành công : " + file.getOriginalFilename();
//
//    }
public String uploadImage(MultipartFile file,Customer customer) throws IOException {
    String originalFilename = file.getOriginalFilename();
    String contentType = file.getContentType();

    // Kiểm tra nếu tên hình ảnh đã tồn tại trong database
    if (imageRepository.existsByName(originalFilename)) {
        throw new AppException(ErrorCode.NAME_IMAGE_EXISTED);
    }


    // Tiến hành lưu ảnh mới
    Image newImage = imageRepository.save(
            Image.builder()
                    .name(originalFilename)
                    .type(contentType)
                    .imageData(ImageUtils.compressImage(file.getBytes())) // Sử dụng file.getBytes() để lưu
                    .customer(customer)
                    .build()
    );

    return "File ảnh upload thành công: " + originalFilename + ". Thông tin khách hàng: " + customer.getName();
}


    public byte[] downloadImage(String fileName){
        Optional<Image> dbImageData = imageRepository.findByName(fileName);
        byte[] images= ImageUtils.decompressImage(dbImageData.get().getImageData());
        return images;
    }
}
