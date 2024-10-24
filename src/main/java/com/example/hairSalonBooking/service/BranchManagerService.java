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
import com.example.hairSalonBooking.model.response.ProfileResponse;
import com.example.hairSalonBooking.repository.AccountRepository;
import com.example.hairSalonBooking.repository.BookingRepository;
import com.example.hairSalonBooking.repository.SalonBranchRepository;
import com.example.hairSalonBooking.repository.ServiceRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    @Autowired
    private ServiceRepository serviceRepository;
    public ManagerResponse createManager(CreateManagerRequest request){
        Account account  = modelMapper.map(request,Account.class);
        try {
            SalonBranch salonBranch = salonBranchRepository.findSalonBranchBySalonId(request.getSalonId());
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
        List<Account> accounts = accountRepository.findByRoleAndIsDeletedFalse(Role.BRANCH_MANAGER);
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
    public ManagerResponse getSpecificManager(long accountId){
        Account account = accountRepository.findAccountByAccountid(accountId);
        ManagerResponse response = new ManagerResponse();
        response.setAccountid(account.getAccountid());
        response.setGender(account.getGender());
        response.setDob(account.getDob());
        response.setEmail(account.getEmail());
        response.setPhone(account.getPhone());
        response.setFullName(account.getFullname());
        response.setSalonAddress(account.getSalonBranch().getAddress());
        return response;
    }
    public ManagerResponse updateManager(long id, UpdateManagerRequest request){
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
    // cái này để lấy tất cả các booking của stylist theo chi nhánh
    public List<BookingResponse> getAllBookingsForStylistsInBranch(Long branchId, LocalDate date) {
        // Kiểm tra xem chi nhánh có tồn tại không
        SalonBranch branch = salonBranchRepository.findById(branchId)
                .orElseThrow(() -> new IllegalArgumentException("Branch not found"));

        // Lấy tất cả các stylist trong chi nhánh
        List<Account> stylists = accountRepository.getStylistsBySalo(branchId);

        // Lấy danh sách booking của các stylist trong chi nhánh
        List<Booking> bookings = new ArrayList<>();
        for(Account account : stylists){
            List<Booking> list = bookingRepository.findAllByAccountInAndSalonBranch(account.getAccountid(), date);
            bookings.addAll(list);
        }
        List<BookingResponse> responses = new ArrayList<>();
        for(Booking booking : bookings){
            Set<SalonService> services = serviceRepository.getServiceForBooking(booking.getBookingId());
            Set<String> serviceNames = new HashSet<>();
            for(SalonService service : services){
                serviceNames.add(service.getServiceName());
            }

            BookingResponse bookingResponse = new BookingResponse();

            bookingResponse.setId(booking.getBookingId());
            bookingResponse.setCustomerId(booking.getAccount().getAccountid());

            bookingResponse.setStylistName(booking.getStylistSchedule().getAccount().getFullname());
            bookingResponse.setTime(booking.getSlot().getSlottime());
            bookingResponse.setDate(booking.getBookingDay());
            bookingResponse.setSalonName(booking.getSalonBranch().getAddress());

            bookingResponse.setServiceName(serviceNames);

            bookingResponse.setStatus(booking.getStatus());
            bookingResponse.setCustomerName(booking.getAccount().getFullname());
            if(booking.getVoucher() != null){
                bookingResponse.setVoucherCode(booking.getVoucher().getCode());
            }

            responses.add(bookingResponse);
        }
        return responses;
    }

    public ProfileResponse getProfile(){
        var context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        Account account =(Account) authentication.getPrincipal();
        ProfileResponse profileResponse = new ProfileResponse();
        profileResponse.setAccountid(account.getAccountid());
        profileResponse.setDob(account.getDob());
        profileResponse.setImage(account.getImage());
        profileResponse.setGender(account.getGender());
        profileResponse.setRole(account.getRole());
        profileResponse.setEmail(account.getEmail());
        profileResponse.setPhone(account.getPhone());
        profileResponse.setFullname(account.getFullname());
        if(account.getSalonBranch() != null){
            profileResponse.setSalonId(account.getSalonBranch().getSalonId());
        }
        return profileResponse;
    }

}
