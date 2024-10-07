package com.example.hairSalonBooking.service;

import com.example.hairSalonBooking.entity.*;
import com.example.hairSalonBooking.enums.BookingStatus;
import com.example.hairSalonBooking.enums.Role;
import com.example.hairSalonBooking.model.request.BookingRequest;
import com.example.hairSalonBooking.model.request.BookingSlots;
import com.example.hairSalonBooking.model.request.BookingStylits;
import com.example.hairSalonBooking.model.response.*;
import com.example.hairSalonBooking.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
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
    public Set<StylistForBooking> getListService(BookingStylits bookingStylits){
        Set<Skill> skills = new HashSet<>();
        for (Long id : bookingStylits.getServiceId()){
            SalonService service = serviceRepository.getServiceById(id);
            Skill skill = skillRepository.findSkillBySkillId(service.getSkill().getSkillId());
            skills.add(skill);
        }
        Set<Account> accounts = new HashSet<>();
        for(Skill skill : skills){
            Set<Account> accounts1 = accountRepository.getAccountBySkill(skill.getSkillId(),bookingStylits.getSalonId());
            if(accounts.isEmpty()){
                accounts.addAll(accounts1);
            }else{
                accounts.retainAll(accounts1);
            }
        }
        Set<StylistForBooking> stylistForBookings = new HashSet<>();
        for(Account account : accounts){
            StylistForBooking response = new StylistForBooking();
            response.setFullname(account.getFullname());
            response.setImage(account.getImage());
            stylistForBookings.add(response);
        }
        return stylistForBookings;
    }

    public List<Slot> getListSlot(BookingSlots bookingSlots){
        List<Booking> allBookingInDay = bookingRepository.getBookingsByStylistInDay(bookingSlots.getDate(),bookingSlots.getAccountId());
        List<Slot> slots = new ArrayList<>();
        List<Slot> slotToRemove = new ArrayList<>();
        if(allBookingInDay.isEmpty()){
            slots = slotRepository.findAll();
            for (Slot slot : slots){
                LocalTime localTime = LocalTime.now();
                LocalDate date = LocalDate.now();
                if(date.isEqual(bookingSlots.getDate())){
                    if(localTime.isAfter(slot.getSlottime())){
                        slotToRemove.add(slot);
                    }
                }
            }
            slots.removeAll(slotToRemove);
        }

        LocalTime totalTimeServiceBooking = totalTimeServiceBooking(bookingSlots.getServiceId());
        for(Booking booking : allBookingInDay){
            // tính tổng thời gian của 1 booking
            LocalTime totalTime = serviceRepository.getTotalTime(booking.getBookingId());
            // lấy ra đc slot của từng booking
            Slot slot = slotRepository.findSlotBySlotid(booking.getSlot().getSlotid());
            // thời gian dự kiến  hoàn thành của cái booking đó
            LocalTime newTime = slot.getSlottime().plusHours(totalTime.getHour())
                    .plusMinutes(totalTime.getMinute());
            if((totalTime.getHour() >= 1) && (totalTime.getMinute() > 0 ) ){
                List<Slot> list = slotRepository.getSlotToRemove(slot.getSlottime(),totalTime.getHour());
                slotToRemove.addAll(list); // 16 17 13
            }
            if((totalTimeServiceBooking.getHour()) > 1 && (slot.getSlotid() != 1)  ){

                LocalTime time = slot.getSlottime().minusHours(totalTimeServiceBooking.getHour())
                        .minusMinutes(totalTimeServiceBooking.getMinute());
                List<Slot> list = slotRepository.getSlotToRemove(time,totalTimeServiceBooking.getHour());
                slotToRemove.addAll(list);
            }
            // Lấy ra các slot có thể booking
            List<Slot> slotCanBooking = slotRepository.getAllSlotCanBooking(newTime);
            // add tất cả slot có thể booking vào slots
                slots = new ArrayList<>(slotCanBooking);
            //                   13    18 19 20 21 22
            //          10    12    14 18 19 20 21 22
            // slots: 9 10 11    13 14 18 19 20 21 22

            slotToRemove.add(slot);// 15 12
        }
        slots.removeAll(slotToRemove);
        //List<Slot> slotToBooking = uniqueSlot(slots);

        return slots;
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
    // chuyển từ Booking sang Booking response
    private List<CusBookingResponse> getBookingResponses(List<Booking> status) {
        return status.stream()
                .map(booking -> {
                    CusBookingResponse response = new CusBookingResponse();
                    response.setSalonName(booking.getSalonBranch() != null ? booking.getSalonBranch().getAddress() : null);
                    response.setStylistName(booking.getStylistSchedule() != null ? booking.getStylistSchedule().getAccount().getFullname() : null);
                    response.setDate(booking.getBookingDay());
                    response.setTime(booking.getSlot() != null ? booking.getSlot().getSlottime() : null);
                    Set<SalonServiceCusResponse> serviceDTOs = booking.getServices().stream()
                            .map(service -> new SalonServiceCusResponse(
                                    service.getServiceName(),
                                    service.getPrice(),
                                    service.getDuration()
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
        List<Booking> status = bookingRepository.findByAccountAndStatus(account, BookingStatus.PENDING);
        return getBookingResponses(status);
    }
    public List<CusBookingResponse> getBookingByStatusIN_PROGRESSByCustomer(Long accountid) {
        Account account = new Account();
        account.setAccountid(accountid);
        List<Booking> status = bookingRepository.findByAccountAndStatus(account, BookingStatus.IN_PROGRESS);
        return getBookingResponses(status);
    }
    public List<CusBookingResponse> getBookingByStatusCompletedByCustomer(Long accountid) {
        Account account = new Account();
        account.setAccountid(accountid);
        List<Booking> status = bookingRepository.findByAccountAndStatus(account, BookingStatus.COMPLETED);
        return getBookingResponses(status);
    }

}
