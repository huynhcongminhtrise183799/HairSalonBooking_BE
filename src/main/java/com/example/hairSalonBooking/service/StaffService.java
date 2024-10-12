package com.example.hairSalonBooking.service;

import com.example.hairSalonBooking.entity.Account;
import com.example.hairSalonBooking.entity.SalonBranch;
import com.example.hairSalonBooking.enums.Role;
import com.example.hairSalonBooking.exception.AppException;
import com.example.hairSalonBooking.exception.ErrorCode;
import com.example.hairSalonBooking.model.request.CreateStaffRequest;
import com.example.hairSalonBooking.model.request.UpdateStaffRequest;
import com.example.hairSalonBooking.model.response.StaffResponse;
import com.example.hairSalonBooking.repository.AccountRepository;
import com.example.hairSalonBooking.repository.SalonBranchRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StaffService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private SalonBranchRepository salonBranchRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    public StaffResponse createStaff(CreateStaffRequest request){
        Account account = modelMapper.map(request,Account.class);
        try {
            SalonBranch salonBranch = salonBranchRepository.findSalonBranchBySalonId(request.getSalonId());
            account.setSalonBranch(salonBranch);
            account.setPassword(passwordEncoder.encode(request.getPassword()));
            account.setRole(Role.STAFF);
            accountRepository.save(account);
            return modelMapper.map(account,StaffResponse.class);
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
    //accountid;
    //    String email;
    //    String username;
    //    String fullName;
    //    LocalDate dob;
    //    String salonAddress;
    //    String phone;
    //    String gender;
    //    boolean isDelete
    public List<StaffResponse> getAllStaffs(){
        List<Account> accounts = accountRepository.getAccountsByRoleSTAFF();
        List<StaffResponse> staffResponses = new ArrayList<>();
        for(Account account : accounts){
            StaffResponse staffResponse = StaffResponse.builder()
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
            staffResponses.add(staffResponse);
        }
        return staffResponses;
    }
    public StaffResponse getSpecificStaff(long accountId){
        Account account = accountRepository.findAccountByAccountid(accountId);
        StaffResponse staffResponse = new StaffResponse();
        staffResponse.setAccountid(account.getAccountid());
        staffResponse.setGender(account.getGender());
        staffResponse.setEmail(account.getEmail());
        staffResponse.setDob(account.getDob());
        staffResponse.setPhone(account.getPhone());
        staffResponse.setFullName(account.getFullname());
        staffResponse.setSalonAddress(account.getSalonBranch().getAddress());
        return staffResponse;
    }
    public StaffResponse updateStaff(UpdateStaffRequest request, long id){
        Account account = accountRepository.findAccountByAccountid(id);
        if(account == null){
            throw new AppException(ErrorCode.ACCOUNT_Not_Found_Exception);
        }

        SalonBranch salonBranch = salonBranchRepository.findSalonBranchBySalonId(request.getSalonId());
        account.setEmail(request.getEmail());
        account.setDob(request.getDob());
        account.setPhone(request.getPhone());
        account.setFullname(request.getFullName());
        account.setSalonBranch(salonBranch);
        account.setGender(request.getGender());
        account.setDeleted(request.isDelete());
        accountRepository.save(account);
        StaffResponse staffResponse = StaffResponse.builder()
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
        return staffResponse;
    }

    public StaffResponse deleteStaff(long id){
        Account account = accountRepository.findAccountByAccountid(id);
        if(account == null){
            throw new AppException(ErrorCode.ACCOUNT_Not_Found_Exception);
        }
        account.setDeleted(true);
        accountRepository.save(account);
        StaffResponse staffResponse = StaffResponse.builder()
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
        return staffResponse;
    }
}
