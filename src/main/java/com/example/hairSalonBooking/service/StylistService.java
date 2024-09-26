package com.example.hairSalonBooking.service;


import com.example.hairSalonBooking.controller.StylistController;
import com.example.hairSalonBooking.entity.Account;
import com.example.hairSalonBooking.enums.Role;
import com.example.hairSalonBooking.exception.AppException;
import com.example.hairSalonBooking.exception.ErrorCode;
import com.example.hairSalonBooking.model.request.StylistRequest;
import com.example.hairSalonBooking.model.response.AccountResponse;
import com.example.hairSalonBooking.model.response.StylistResponse;
import com.example.hairSalonBooking.repository.AccountRepository;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StylistService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    ModelMapper modelMapper;

    public StylistResponse create(StylistRequest stylistRequest) {
        // Chuyển từ StylistRequest sang thực thể Account (hoặc Stylist)
        Account stylist = modelMapper.map(stylistRequest, Account.class);
        try{
            // Lưu vào database
            Account newStylist = accountRepository.save(stylist);

            // Chuyển đổi để trả về response
            return modelMapper.map(newStylist, StylistResponse.class);
        }catch(Exception e) {
            if(e.getMessage().contains(stylist.getUsername())) {
                throw new AppException(ErrorCode.USERNAME_EXISTED);
            }else if(e.getMessage().contains(stylist.getEmail())) {
                throw new AppException(ErrorCode.EMAIL_EXISTED);
            }else {
                throw new AppException(ErrorCode.Phone_EXISTED);
            }
        }
    }

    public List<StylistResponse> getAllStylist() {
        List<Account> Stylists = accountRepository.findAll();
        return Stylists.stream() // Chuyển đổi sang danh sách StylistResponse
                .map(account -> modelMapper.map(account, StylistResponse.class))
                .collect(Collectors.toList());// Thu thập kết quả vào danh sách
    }


    public StylistResponse updateStylist(long accountid, StylistRequest stylistRequest) {
        //tìm ra thằng stylist cần đc update thông qua ID
        Account updeStylist = accountRepository.findAccountByAccountid(accountid);
        if (updeStylist == null) {
            // Handle the case when the stylist is not found
            throw new AppException(ErrorCode.STYLIST_NOT_FOUND);
        }
        //Cập nhập thông tin nó
        updeStylist.setUsername(stylistRequest.getUsername());
        updeStylist.setEmail(stylistRequest.getEmail());
        updeStylist.setFullname(stylistRequest.getFullname());
        updeStylist.setPhone(stylistRequest.getPhone());
        updeStylist.setGender(stylistRequest.getGender());
        updeStylist.setSalonid(stylistRequest.getSalonid());
        updeStylist.setLevelid(stylistRequest.getLevelid());


        //Làm xong thì lưu xuống DataBase
        Account updatedStylist = accountRepository.save(updeStylist);
        // trả về thôi
        return modelMapper.map(updatedStylist, StylistResponse.class);
    }

    public StylistResponse deleteStylist(long accountid) {
        // đầu tiên mình tìm thằng cần Delete qua ID
        Account updeStylist = accountRepository.findAccountByAccountid(accountid);
        if (updeStylist == null) {
            // Handle the case when the stylist is not found
            throw new AppException(ErrorCode.STYLIST_NOT_FOUND);
        }
        updeStylist.setDeleted(true);
        Account deleteStylist = accountRepository.save(updeStylist);
        return modelMapper.map(deleteStylist, StylistResponse.class);
    }

}
