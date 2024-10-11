package com.example.hairSalonBooking.service;


import com.example.hairSalonBooking.entity.*;
import com.example.hairSalonBooking.enums.Role;
import com.example.hairSalonBooking.exception.AppException;
import com.example.hairSalonBooking.exception.ErrorCode;
import com.example.hairSalonBooking.model.request.StylistRequest;
import com.example.hairSalonBooking.model.request.UpdateStylistRequest;
import com.example.hairSalonBooking.model.response.*;
import com.example.hairSalonBooking.repository.*;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    @Autowired
    private BookingRepository bookingRepository;

    public StylistResponse create(StylistRequest stylistRequest) {
        // Chuyển từ StylistRequest sang thực thể Account
        Account stylist = modelMapper.map(stylistRequest, Account.class);
        stylist.setRole(Role.STYLIST);
        try{
            stylist.setPassword(passwordEncoder.encode(stylistRequest.getPassword()));
            SalonBranch salonBranch = salonBranchRepository.findSalonBranchBySalonIdAndIsDeleteFalse(stylistRequest.getSalonId());
            stylist.setSalonBranch(salonBranch);
            Level level  = levelRepository.findLevelByLevelid(stylistRequest.getLevelId());
            stylist.setLevel(level);
            stylist.setRole(Role.STYLIST);
            Set<Skill> skills = new HashSet<>();
            Set<String> skillNames = new HashSet<>();
            // Lưu vào database
            for(Long id : stylistRequest.getSkillId()){
                Skill skill = skillRepository.findSkillBySkillId(id);
                skillNames.add(skill.getSkillName());
                skills.add(skill);
            }
            stylist.setSkills(skills);
            stylist.setImage(stylistRequest.getImage());
            // Lưu vào database
            Account newStylist = accountRepository.save(stylist);
            StylistResponse stylistResponse = new StylistResponse();
            stylistResponse.setUsername(newStylist.getUsername());
            stylistResponse.setAccountid(newStylist.getAccountid());
            stylistResponse.setEmail(newStylist.getEmail());
            stylistResponse.setFullname(newStylist.getFullname());
            stylistResponse.setPhone(newStylist.getPhone());
            stylistResponse.setImage(newStylist.getImage());
            stylistResponse.setDob(newStylist.getDob());
            stylistResponse.setGender(newStylist.getGender());
            stylistResponse.setSalonAddress(newStylist.getSalonBranch().getAddress());
            stylistResponse.setLevelName(newStylist.getLevel().getLevelname());
           stylistResponse.setSkillName(skillNames);

            // Chuyển đổi để trả về response
            return stylistResponse;
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

    public StylistPageResponse getAllAccountStylist(int page, int size) {
        Page<Account> accountPage = accountRepository.findByRole(Role.STYLIST, PageRequest.of(page, size));


        Page<StyPageResponse> stylistPage = accountPage.map(account ->
                new StyPageResponse(
                        account.getAccountid(),
                        account.getUsername(),
                        account.getEmail(),
                        account.getFullname(),
                        account.getPhone(),
                        account.getImage(),
                        account.getDob(),
                        account.getGender(),
                        account.getSalonBranch().getAddress(),
                        account.getLevel().getLevelname(),
                        account.getSkills())
        );

        // Building the AccountPageResponse using stylistPageResponse
        StylistPageResponse stylistPageResponse = new StylistPageResponse();
        stylistPageResponse.setPageNumber(stylistPage.getNumber()); // Set the correct Page number
        stylistPageResponse.setTotalPages(stylistPage.getTotalPages()); // Set total pages
        stylistPageResponse.setTotalElements(stylistPage.getTotalElements()); // Set total elements
        stylistPageResponse.setContent(stylistPage.getContent()); // Set the list of StylistResponse DTOs

        return stylistPageResponse;
    }

    public List<StylistResponse> getStylistByStatus() {
        List<Account> StylistStatus = accountRepository.findByRoleAndIsDeletedFalse(Role.STYLIST);
        return StylistStatus.stream()
                .map(account -> modelMapper.map(account, StylistResponse.class))
                .collect(Collectors.toList());
    }

    public StylistResponse updateStylist(long accountid, UpdateStylistRequest stylistRequest) {
        //tìm ra thằng stylist cần đc update thông qua ID
        Account updeStylist = accountRepository.findAccountByAccountid(accountid);
        updeStylist.setRole(Role.STYLIST);
        if (updeStylist == null) {
            // Handle the case when the stylist is not found
            throw new AppException(ErrorCode.STYLIST_NOT_FOUND);
        }
        accountRepository.deleteSpecificSkills(accountid);
        SalonBranch salonBranch = salonBranchRepository.findSalonBranchBySalonIdAndIsDeleteFalse(stylistRequest.getSalonId());
        updeStylist.setSalonBranch(salonBranch);
        Level level  = levelRepository.findLevelByLevelid(stylistRequest.getLevelId());
        updeStylist.setLevel(level);
        Set<Skill> skills = new HashSet<>();
        Set<String> skillNames = new HashSet<>();
        // Lưu vào database
        for(Long id : stylistRequest.getSkillId()){
            Skill skill = skillRepository.findSkillBySkillId(id);
            skillNames.add(skill.getSkillName());
            skills.add(skill);
        }
        updeStylist.setSkills(skills);
        //Cập nhập thông tin nó
        updeStylist.setEmail(stylistRequest.getEmail());
        updeStylist.setFullname(stylistRequest.getFullname());
        updeStylist.setPhone(stylistRequest.getPhone());
        updeStylist.setGender(stylistRequest.getGender());
        updeStylist.setImage(stylistRequest.getImage());
        updeStylist.setDeleted(stylistRequest.isDelete());
        updeStylist.setDob(stylistRequest.getDob());
        //Làm xong thì lưu xuống DataBase
        Account updatedStylist = accountRepository.save(updeStylist);
        StylistResponse stylistResponse = new StylistResponse();
        stylistResponse.setUsername(updatedStylist.getUsername());
        stylistResponse.setAccountid(updatedStylist.getAccountid());
        stylistResponse.setEmail(updatedStylist.getEmail());
        stylistResponse.setFullname(updatedStylist.getFullname());
        stylistResponse.setPhone(updatedStylist.getPhone());
        stylistResponse.setImage(updatedStylist.getImage());
        stylistResponse.setDob(updatedStylist.getDob());
        stylistResponse.setGender(updatedStylist.getGender());
        stylistResponse.setSalonAddress(updatedStylist.getSalonBranch().getAddress());
        stylistResponse.setLevelName(updatedStylist.getLevel().getLevelname());
        stylistResponse.setSkillName(skillNames);
        // trả về thôi
        return stylistResponse;
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
    public List<BookingResponse> getBookingsForStylistOnDate(Long stylistId, LocalDate date) {
        // Kiểm tra xem account có phải là Stylist không
        Account stylist = accountRepository.findById(stylistId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        // Kiểm tra Role
        if (stylist.getRole() != Role.STYLIST) {
            throw new IllegalArgumentException("Account is not a Stylist");
        }

        // Lấy danh sách booking từ database
        List<Booking> bookings = bookingRepository.findAllByAccountInAndSalonBranch(stylistId, date);
        // Chuyển đổi lúc trả ra từ Booking sang BookingResponse
        List<BookingResponse> responses = new ArrayList<>();
        for(Booking booking : bookings){
            Set<String> serviceNames = serviceRepository.getServiceNameByBooking(booking.getBookingId());
            BookingResponse bookingResponse = new BookingResponse();
            bookingResponse.setStylistName(booking.getStylistSchedule().getAccount().getFullname());
            bookingResponse.setTime(booking.getSlot().getSlottime());
            bookingResponse.setDate(booking.getBookingDay());
            bookingResponse.setSalonName(booking.getSalonBranch().getAddress());
            bookingResponse.setServiceName(serviceNames);
            responses.add(bookingResponse);
        }
        return responses;
    }

}