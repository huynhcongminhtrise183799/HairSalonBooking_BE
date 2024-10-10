package com.example.hairSalonBooking.service;

import com.example.hairSalonBooking.entity.*;
import com.example.hairSalonBooking.enums.BookingStatus;
import com.example.hairSalonBooking.enums.Role;
import com.example.hairSalonBooking.exception.AppException;
import com.example.hairSalonBooking.exception.ErrorCode;
import com.example.hairSalonBooking.model.request.BookingRequest;
import com.example.hairSalonBooking.model.request.BookingSlots;
import com.example.hairSalonBooking.model.request.BookingStylits;

import com.example.hairSalonBooking.model.response.ShiftResponse;
import com.example.hairSalonBooking.model.response.StylistForBooking;
import com.example.hairSalonBooking.repository.*;
import jdk.jfr.Frequency;

import com.example.hairSalonBooking.model.response.*;
import com.example.hairSalonBooking.repository.*;
import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

import java.util.*;
import java.util.Collections;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private SkillRepository skillRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private SlotRepository slotRepository;
    @Autowired
    private SalonBranchRepository salonBranchRepository;
    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    private StylistScheduleRepository stylistScheduleRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    private ShiftRepository shiftRepository;
    public Set<StylistForBooking> getStylistForBooking(BookingStylits bookingStylits){
        // tao 1 danh sach skills rong
        Set<Skill> skills = new HashSet<>();
        // lặp để lấy ra từng thằng service tương ứng -> skill tương ứng và add vào skills
        for (Long id : bookingStylits.getServiceId()){
            SalonService service = serviceRepository.getServiceById(id);
            Skill skill = skillRepository.findSkillBySkillId(service.getSkill().getSkillId());
            skills.add(skill);
        }
        // tạo ra 1 danh sách accounts rỗng
        Set<Account> accounts = new HashSet<>();
        // duyệt qua từng skill trong list skills
        for(Skill skill : skills){ // vd: skill 1 2
            // lấy ra hết những thằng stylist có skill đó và trong chi nhánh đưa vào
            // 2 3 5
            //   3 5
            Set<Account> allAccountHaveSkill = accountRepository.getAccountBySkill(skill.getSkillId(),bookingStylits.getSalonId());
            // nếu accounts đó đang trống thì add toàn bộ allAccountHaveSkill vào
            if(accounts.isEmpty()){
                accounts.addAll(allAccountHaveSkill);
                // acounts : 2 3 5
            }else{
                accounts.retainAll(allAccountHaveSkill); // 3 5
            }
        }
        // tạo ra 1 set stylistForBookings -> map từ accounts qua stylistForBookings
        Set<StylistForBooking> stylistForBookings = new HashSet<>();
        for(Account account : accounts){
            StylistForBooking response = new StylistForBooking();
            response.setId(account.getAccountid());
            response.setFullname(account.getFullname());
            response.setImage(account.getImage());
            stylistForBookings.add(response);
        }
        return stylistForBookings;
    }

    public List<Slot> getListSlot(BookingSlots bookingSlots){
        List<Slot> allSlot = slotRepository.findAll();
        List<Slot> slotToRemove = new ArrayList<>();
        List<Shift> shifts = new ArrayList<>();
        List<Shift> shiftsFromSpecificStylistSchedule = shiftRepository.getShiftsFromSpecificStylistSchedule(bookingSlots.getAccountId(),bookingSlots.getDate());
        List<Shift> shiftMissingInSpecificStylistSchedule = shiftMissingInSpecificStylistSchedule(shiftsFromSpecificStylistSchedule);
        if(!shiftMissingInSpecificStylistSchedule.isEmpty()){
            for(Shift shift :shiftMissingInSpecificStylistSchedule ){
                List<Slot> slot = slotRepository.getSlotsInShift(shift.getShiftId());
                slotToRemove.addAll(slot);
            }
            if(slotToRemove.size() == allSlot.size()){
                allSlot.removeAll(slotToRemove);
                return allSlot;
            }
        }
        // lấy được tất cả booking trong ngày của stylist đc truyền vào
        List<Booking> allBookingInDay = bookingRepository.getBookingsByStylistInDay(bookingSlots.getDate(),bookingSlots.getAccountId());
        // lấy ra tất cả các slot có trong database
        for (Slot slot : allSlot){
            // duyệt qua từng slot xét xem coi thời gian thực có qua thời gian của slot đó chưa
            LocalTime localTime = LocalTime.now();
            LocalDate date = LocalDate.now();
            if(date.isEqual(bookingSlots.getDate())){
                // nếu thời gian thực qua thời gian của slot đó r thì add slot đó vào 1 cái list slotToRemove
                if(localTime.isAfter(slot.getSlottime())){
                    slotToRemove.add(slot);
                }else{
                    break;
                }
            }
        }
        // nếu stylist đó chưa có booking nào trong ngày
        if(allBookingInDay.isEmpty()){
            // xóa tất cả thằng có trong slotToRemove
            allSlot.removeAll(slotToRemove);
            return allSlot;
        }
        // stylist đã có booking trong ngày
        // tính tổng thời gian để hoàn thành yêu cầu booking mới
        LocalTime totalTimeServiceNewBooking = totalTimeServiceBooking(bookingSlots.getServiceId());
        // duyệt qua từng booking có trong list allBookingInDay
        for(Booking booking : allBookingInDay){ // vd: lấy đc booking có ID là 1
            // tính tổng thời gian của tất cả service của 1 booking vd: tổng thời gian để hoàn thành
                                                                        // tất cả service có
                                                                        //  trong booking đó là 1:30:00
            LocalTime totalTimeServiceForBooking = serviceRepository.getTotalTime(booking.getBookingId());
            // lấy ra đc slot cụ thể của từng booking vd: slot 1 -> thời gian là 8:00:00
            Slot slot = slotRepository.findSlotBySlotid(booking.getSlot().getSlotid());
            // thời gian dự kiến  hoàn thành của cái booking đó vd: 9:30:00 do slot bắt đầu là 8h
                                                                // và thời gian hoàn thành tất cả service là 1:30
            LocalTime TimeFinishBooking = slot.getSlottime().plusHours(totalTimeServiceForBooking.getHour())
                    .plusMinutes(totalTimeServiceForBooking.getMinute());
            // Xét nếu thời gian của tất cả service của 1 booking đó có lớn hơn 1 tiếng không
            if(totalTimeServiceForBooking.getHour() >= 1 ){
                // lấy ra list các slot booking ko hợp lệ
                // vd: 8:00:00 bắt đầu và thời gian hoàn thành là 9:30:00 thì
                // slot bắt đầu 9:00:00 là ko hợp lệ sẽ bị add vào list slotToRemove
                if(totalTimeServiceForBooking.getMinute() == 0 ){
                    List<Slot> list = slotRepository.getSlotToRemove(slot.getSlottime(),totalTimeServiceForBooking.getHour() - 1 );
                    slotToRemove.addAll(list); // 9
                }else{
                    List<Slot> list = slotRepository.getSlotToRemove(slot.getSlottime(),totalTimeServiceForBooking.getHour());
                    slotToRemove.addAll(list); // 9
                }
            }
            // tính ra thời gian
            // vd: slot 10h có ng đặt r, tổng thời gian service cho booking mới là 1h30p
            // tối thiểu phải là 8h30p mới đc đặt mà do slot ko có 8h30p
            // thì có nghĩa là 8h thỏa thời gian booking này và slot 9h ko thỏa
            LocalTime minimunTimeToBooking = slot.getSlottime().minusHours(totalTimeServiceNewBooking.getHour())
                        .minusMinutes(totalTimeServiceNewBooking.getMinute());
            // tìm ra list chứa các slot ko thỏa và add vào list slotToRemove
            List<Slot> list = slotRepository.getSlotToRemove(minimunTimeToBooking,totalTimeServiceNewBooking.getHour());
            slotToRemove.addAll(list);
            slotToRemove.add(slot);// 10 11
            // tìm ra list ca làm mà cái booking đó thuộc về
            List<Shift> bookingBelongToShifts = shiftRepository.getShiftForBooking(slot.getSlottime(),TimeFinishBooking,booking.getBookingId());
            // add list vừa tìm đc vào list shifts
            shifts.addAll(bookingBelongToShifts);
        }
        // tìm xem có ca làm nào đạt limitBooking chưa
        List<Shift> shiftsReachedBookingLimit = shiftReachedBookingLimit(shifts);
        for(Shift shift : shiftsReachedBookingLimit){
            // Tìm ra đc các slots thuộc về ca làm đó
            List<Slot> slots = slotRepository.getSlotsInShift(shift.getShiftId());
            // add list vừa tìm đc vào slotToRemove
            slotToRemove.addAll(slots);
        }
        allSlot.removeAll(slotToRemove);
        return allSlot;
    }
    public BookingRequest createNewBooking(BookingRequest request){
        Account account = accountRepository.findAccountByAccountid(request.getCustomerId());
        Set<SalonService> services = new HashSet<>();
        for(Long id : request.getServiceId()){
            SalonService service = serviceRepository.getServiceById(id);
            services.add(service);
        }
        Slot slot = slotRepository.findSlotBySlotid(request.getSlotId());
        SalonBranch salonBranch = salonBranchRepository.findSalonBranchBySalonId(request.getSalonId());
        Voucher voucher = voucherRepository.findVoucherByVoucherId(request.getVoucherId());
        if(voucher != null){
            voucher.setQuantity(voucher.getQuantity() - 1 );
        }
        StylistSchedule stylistSchedule = stylistScheduleRepository.getScheduleId(request.getStylistId(), request.getBookingDate());
        Booking booking = new Booking();
        booking.setBookingDay(request.getBookingDate());
        booking.setAccount(account);
        booking.setSlot(slot);
        booking.setSalonBranch(salonBranch);
        booking.setServices(services);
        booking.setVoucher(voucher);
        booking.setStylistSchedule(stylistSchedule);
        booking.setStatus(BookingStatus.PENDING);
        bookingRepository.save(booking);
        return request;
    }
    public BookingRequest updateBooking(long bookingId,BookingRequest request){
        Booking booking = bookingRepository.findBookingByBookingId(bookingId);
        bookingRepository.deleteBookingDetail(booking.getBookingId());
        Account account = accountRepository.findAccountByAccountid(request.getCustomerId());
        Set<SalonService> services = new HashSet<>();
        for(Long id : request.getServiceId()){
            SalonService service = serviceRepository.getServiceById(id);
            services.add(service);
        }
        Slot slot = slotRepository.findSlotBySlotid(request.getSlotId());
        SalonBranch salonBranch = salonBranchRepository.findSalonBranchBySalonId(request.getSalonId());
        if(booking.getVoucher() != null){
            Voucher oldVoucher = voucherRepository.findVoucherByVoucherId(booking.getVoucher().getVoucherId());
            oldVoucher.setQuantity(oldVoucher.getQuantity() + 1);
        }
        Voucher newVoucher = voucherRepository.findVoucherByVoucherId(request.getVoucherId());
        if(newVoucher != null){
            newVoucher.setQuantity(newVoucher.getQuantity() - 1 );
        }
        StylistSchedule stylistSchedule = stylistScheduleRepository.getScheduleId(request.getStylistId(), request.getBookingDate());
        booking.setBookingDay(request.getBookingDate());
        booking.setAccount(account);
        booking.setSlot(slot);
        booking.setSalonBranch(salonBranch);
        booking.setServices(services);
        booking.setVoucher(newVoucher);
        booking.setStylistSchedule(stylistSchedule);
        booking.setStatus(BookingStatus.PENDING);
        bookingRepository.save(booking);
        return request;
    }
    public String deleteBooking(long id){
        Booking booking = bookingRepository.findBookingByBookingId(id);
        if(booking == null){
            throw new AppException(ErrorCode.BOOKING_NOT_FOUND);
        }
        if(booking.getVoucher() != null){
            Voucher voucher = voucherRepository.findVoucherByVoucherId(booking.getVoucher().getVoucherId());
            voucher.setQuantity(voucher.getQuantity() + 1 );
        }
        bookingRepository.delete(booking);
        return "booking deleted";
    }
    private LocalTime totalTimeServiceBooking(Set<Long> serviceId){
        LocalTime totalTimeDuration = LocalTime.of(0,0,0);
        for(Long id : serviceId){
            SalonService service = serviceRepository.getServiceById(id);
            LocalTime duration = service.getDuration();
            totalTimeDuration = totalTimeDuration.plusHours(duration.getHour())
                    .plusMinutes(duration.getMinute());
        }
        return totalTimeDuration;
    }

    private List<Shift> shiftReachedBookingLimit(List<Shift> shifts){
        List<Shift> list = new ArrayList<>();
        // tạo set vì trong set ko có phần tử trùng lặp
        Set<Shift> set = new HashSet<>(shifts);
        for(Shift shift : set){
            // đếm số lần shift xuất hiện trong shifts
            int totalBookingInShift = Collections.frequency(shifts,shift);
            // nếu totalBookingInShift == limit booking thì add shift đó vào list  
            if(totalBookingInShift == shift.getLimitBooking()){
                list.add(shift);
            }
        }
        return list;
    }
    private List<Shift> shiftMissingInSpecificStylistSchedule(List<Shift> shifts) {
        List<Shift> allShift = shiftRepository.findAll();
        allShift.removeAll(shifts);
        return allShift;
    }
    private List<CusBookingResponse> getBookingResponses(List<Booking> status) {
        return status.stream()
                .map(booking -> {
                    CusBookingResponse response = new CusBookingResponse();
                    response.setBookingId(booking.getBookingId());
                    response.setSalonName(booking.getSalonBranch() != null ? booking.getSalonBranch().getAddress() : null);
                    response.setStylistName(booking.getStylistSchedule() != null ? booking.getStylistSchedule().getAccount().getFullname() : null);
                    response.setDate(booking.getBookingDay());
                    response.setTime(booking.getSlot() != null ? booking.getSlot().getSlottime() : null);
                    Set<SalonServiceCusResponse> serviceDTOs = booking.getServices().stream()
                            .map(service -> new SalonServiceCusResponse(
                                    service.getServiceName()
//                                    service.getPrice(),
//                                    service.getDuration()
                            ))
                            .collect(Collectors.toSet());
                    response.setServiceName(serviceDTOs);
                    response.setStatus(booking.getStatus());
                    return response;
                })
                .collect(Collectors.toList());
    }

    public List<CusBookingResponse> getBookingByStatusPendingByCustomer(Long accountid) {
        Account account = new Account();
        account.setAccountid(accountid);
        //List<Booking> status = bookingRepository.findByAccountAndStatus(account, BookingStatus.PENDING);
        List<Booking> status = new ArrayList<>();
        // lấy list booking truyền vô database lay chuoi enium duoi. name de query duoi database
        List<Booking> bookings =bookingRepository.getBookingsByIdAndSatus(accountid, BookingStatus.PENDING.name());
        for(Booking booking : bookings){
            Set<SalonService> service = serviceRepository.getServiceForBooking(booking.getBookingId());
            booking.setServices(service);
            status.add(booking);
        }
        return getBookingResponses(status);
    }
    public List<CusBookingResponse> getBookingByStatusCompletedByCustomer(Long accountid) {
        Account account = new Account();
        account.setAccountid(accountid);
        List<Booking> status = new ArrayList<>();
        List<Booking> bookings =bookingRepository.getBookingsByIdAndSatus(accountid, BookingStatus.COMPLETED.name());
        for(Booking booking : bookings){
            Set<SalonService> service = serviceRepository.getServiceForBooking(booking.getBookingId());
            booking.setServices(service);
            status.add(booking);
        }
        return getBookingResponses(status);
    }
    public String checkIn(long bookingId){
        Booking booking = bookingRepository.findBookingByBookingId(bookingId);
        if(booking == null){
            throw new AppException(ErrorCode.BOOKING_NOT_FOUND);
        }
        booking.setStatus(BookingStatus.IN_PROGRESS);
        bookingRepository.save(booking);
        return "check-in success";
    }
}
