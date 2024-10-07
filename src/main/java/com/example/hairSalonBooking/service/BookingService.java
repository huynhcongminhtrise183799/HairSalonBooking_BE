package com.example.hairSalonBooking.service;

import com.example.hairSalonBooking.entity.*;
import com.example.hairSalonBooking.enums.Role;
import com.example.hairSalonBooking.model.request.BookingRequest;
import com.example.hairSalonBooking.model.request.BookingSlots;
import com.example.hairSalonBooking.model.request.BookingStylits;
import com.example.hairSalonBooking.model.response.BookingResponse;
import com.example.hairSalonBooking.model.response.StylistForBooking;
import com.example.hairSalonBooking.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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



    // Cái này là để in ra booking của stylist theo ngày mình chọn
    // Lấy danh sách booking trong ngày của stylist dựa vào accountId và role Stylist
    public List<BookingResponse> getBookingsForStylistOnDate(Long stylistId,LocalDate date) {
        // Kiểm tra xem account có phải là Stylist không
        Account stylist = accountRepository.findById(stylistId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        // Kiểm tra Role
        if (stylist.getRole() != Role.STYLIST) {
            throw new IllegalArgumentException("Account is not a Stylist");
        }

        // Lấy danh sách booking từ database
        List<Booking> bookings = bookingRepository.findBookingsByStylistAndDate(stylistId, date);
        // Chuyển đổi lúc trả ra từ Booking sang BookingResponse
        return bookings.stream()
                .map(booking -> new BookingResponse(
                        booking.getSalonBranch().getAddress(), // lấy getAddress()
                        stylist.getFullname(),                  // lấy getFullname()
                        booking.getStylistSchedule().getWorkingDay(),                //  lấy getBookingDay()
                        booking.getSlot().getSlottime(),       // lấy getSlottime()
                        booking.getServices().stream()
                                .map(SalonService::getServiceName) // Assuming getServiceName() returns service name
                                .collect(Collectors.toSet())
                ))
                .collect(Collectors.toList());
    }
}
