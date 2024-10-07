package com.example.hairSalonBooking.service;


import com.example.hairSalonBooking.entity.*;
import com.example.hairSalonBooking.enums.Role;
import com.example.hairSalonBooking.exception.AppException;
import com.example.hairSalonBooking.exception.ErrorCode;
import com.example.hairSalonBooking.model.request.StylistRequest;
import com.example.hairSalonBooking.model.response.StylistResponse;
import com.example.hairSalonBooking.repository.*;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StylistService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    private SalonBranchRepository salonBranchRepository;

    @Autowired
    private LevelRepository levelRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SkillRepository skillRepository;
    @Autowired
    private ServiceRepository serviceRepository;



    public StylistResponse create(StylistRequest stylistRequest) {
        // Chuyển từ StylistRequest sang thực thể Account
        Account stylist = modelMapper.map(stylistRequest, Account.class);
        try{
            stylist.setPassword(passwordEncoder.encode(stylistRequest.getPassword()));
            SalonBranch salonBranch = salonBranchRepository.findSalonBranchByAddressIsDeleteFalse(stylistRequest.getSalonAddress());
            stylist.setSalonBranch(salonBranch);
            Level level  = levelRepository.findLevelByLevelname(stylistRequest.getLevelName());
            stylist.setLevel(level);
            stylist.setRole(Role.STYLIST);
            Set<Skill> skills = new HashSet<>();
            // Lưu vào database
            for(Long id : stylistRequest.getSkillId()){
                Skill skill = skillRepository.findSkillBySkillId(id);
                skills.add(skill);
            }
            stylist.setSkills(skills);
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
        List<Account> stylists = accountRepository.findByRole(Role.STYLIST);
        return stylists.stream() // Chuyển đổi sang danh sách StylistResponse
                .map(account -> modelMapper.map(account, StylistResponse.class))
                .collect(Collectors.toList()); // Thu thập kết quả vào danh sách
    }

    public List<StylistResponse> getStylistByStatus() {
        List<Account> StylistStatus = accountRepository.findByRoleAndIsDeletedFalse(Role.STYLIST);
        return StylistStatus.stream()
                .map(account -> modelMapper.map(account, StylistResponse.class))
                .collect(Collectors.toList());
    }

    public StylistResponse updateStylist(long accountid, StylistRequest stylistRequest) {
        //tìm ra thằng stylist cần đc update thông qua ID
        Account updeStylist = accountRepository.findAccountByAccountid(accountid);
        updeStylist.setRole(Role.STYLIST);
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
        updeStylist.setDeleted(false);


        //Làm xong thì lưu xuống DataBase
        Account updatedStylist = accountRepository.save(updeStylist);
        // trả về thôi
        return modelMapper.map(updatedStylist, StylistResponse.class);
    }

    public StylistResponse deleteStylist(long accountid) {
        // đầu tiên mình tìm thằng cần Delete qua ID
        Account updeStylist = accountRepository.findAccountByAccountid(accountid);
        updeStylist.setRole(Role.STYLIST);
        if (updeStylist == null) {
            // Handle the case when the stylist is not found
            throw new AppException(ErrorCode.STYLIST_NOT_FOUND);
        }
        updeStylist.setDeleted(true);
        Account deleteStylist = accountRepository.save(updeStylist);
        return modelMapper.map(deleteStylist, StylistResponse.class);
    }




}