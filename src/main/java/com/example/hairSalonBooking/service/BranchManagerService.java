package com.example.hairSalonBooking.service;

import com.example.hairSalonBooking.entity.Account;
import com.example.hairSalonBooking.entity.Booking;
import com.example.hairSalonBooking.entity.SalonBranch;
import com.example.hairSalonBooking.entity.SalonService;
import com.example.hairSalonBooking.enums.Role;
import com.example.hairSalonBooking.exception.AppException;
import com.example.hairSalonBooking.exception.ErrorCode;
import com.example.hairSalonBooking.model.request.CreateManagerRequest;
import com.example.hairSalonBooking.model.request.UpdateManagerRequest;
import com.example.hairSalonBooking.model.response.BookingResponse;
import com.example.hairSalonBooking.model.response.ManagerResponse;
import com.example.hairSalonBooking.repository.AccountRepository;
import com.example.hairSalonBooking.repository.BookingRepository;
import com.example.hairSalonBooking.repository.SalonBranchRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BranchManagerService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private SalonBranchRepository salonBranchRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private BookingRepository bookingRepository;

    public ManagerResponse createManager(CreateManagerRequest request){
        Account account  = modelMapper.map(request,Account.class);
        try {
            SalonBranch salonBranch = salonBranchRepository.findSalonBranchByAddressIsDeleteFalse(request.getSalonAddress());
            account.setSalonBranch(salonBranch);
            account.setRole(Role.BRANCH_MANAGER);
            account.setPassword(passwordEncoder.encode(request.getPassword()));
            accountRepository.save(account);
            return modelMapper.map(account,ManagerResponse.class);
        }catch (Exception e){
            if(e.getMessage().contains(request.getUsername())){
                throw  new AppException(ErrorCode.USERNAME_EXISTED);
            }else if(e.getMessage().contains(request.getEmail())){
                throw new AppException(ErrorCode.EMAIL_EXISTED);
            }else{
                throw new AppException(ErrorCode.Phone_EXISTED);
            }
        }
    }

    public List<ManagerResponse> getAllManagers(){
        List<Account> accounts = accountRepository.getAccountsByRoleManager();
        List<ManagerResponse> managerResponses = new ArrayList<>();
        for(Account account : accounts){
            ManagerResponse response = ManagerResponse.builder()
                    .accountid(account.getAccountid())
                    .email(account.getEmail())
                    .username(account.getUsername())
                    .fullName(account.getFullname())
                    .dob(account.getDob())
                    .salonAddress(account.getSalonBranch().getAddress())
                    .phone(account.getPhone())
                    .gender(account.getGender())
                    .isDelete(account.isDeleted())
                    .build();
            managerResponses.add(response);
        }
        return managerResponses;
    }

    public ManagerResponse updateManager(long id, UpdateManagerRequest request){
        Account account = accountRepository.findAccountByAccountid(id);
        if(account == null){
            throw new AppException(ErrorCode.ACCOUNT_Not_Found_Exception);
        }
        SalonBranch salonBranch = salonBranchRepository.findSalonBranchByAddressIsDeleteFalse(request.getSalonAddress());
        account.setEmail(request.getEmail());
        account.setDob(request.getDob());
        account.setPhone(request.getPhone());
        account.setFullname(request.getFullName());
        account.setSalonBranch(salonBranch);
        account.setGender(request.getGender());
        account.setDeleted(request.isDelete());
        accountRepository.save(account);
        ManagerResponse response = ManagerResponse.builder()
                .accountid(account.getAccountid())
                .email(account.getEmail())
                .username(account.getUsername())
                .fullName(account.getFullname())
                .dob(account.getDob())
                .salonAddress(account.getSalonBranch().getAddress())
                .phone(account.getPhone())
                .gender(account.getGender())
                .isDelete(account.isDeleted())
                .build();
        return response;
    }

    public ManagerResponse deleteManager(long id){
        Account account = accountRepository.findAccountByAccountid(id);
        if(account == null){
            throw new AppException(ErrorCode.ACCOUNT_Not_Found_Exception);
        }
        account.setDeleted(true);
        accountRepository.save(account);
        ManagerResponse response = ManagerResponse.builder()
                .accountid(account.getAccountid())
                .email(account.getEmail())
                .username(account.getUsername())
                .fullName(account.getFullname())
                .dob(account.getDob())
                .salonAddress(account.getSalonBranch().getAddress())
                .phone(account.getPhone())
                .gender(account.getGender())
                .isDelete(account.isDeleted())
                .build();
        return response;
    }

    public List<BookingResponse> getAllBookingsForStylistsInBranch(Long branchId) {
        // Kiểm tra xem chi nhánh có tồn tại không
        SalonBranch branch = salonBranchRepository.findById(branchId)
                .orElseThrow(() -> new IllegalArgumentException("Branch not found"));

        // Lấy tất cả các stylist trong chi nhánh
        List<Account> stylists = accountRepository.findAllByRole(Role.STYLIST);

        // Lấy danh sách booking của các stylist trong chi nhánh
        List<Booking> bookings = bookingRepository.findAllByAccountInAndSalonBranch(stylists, branch);

        // Chuyển đổi danh sách Booking thành BookingResponse
        return bookings.stream()
                .map(booking -> new BookingResponse(
                        booking.getSalonBranch().getAddress(),
                        booking.getAccount().getFullname(),
                        booking.getBookingDay(),
                        booking.getSlot().getSlottime(),
                        booking.getServices().stream()
                                .map(SalonService::getServiceName)
                                .collect(Collectors.toSet())
                ))
                .collect(Collectors.toList());
    }
}
