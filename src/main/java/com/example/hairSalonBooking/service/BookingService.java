package com.example.hairSalonBooking.service;

import com.example.hairSalonBooking.entity.*;
import com.example.hairSalonBooking.model.request.BookingRequest;
import com.example.hairSalonBooking.model.request.BookingSlots;
import com.example.hairSalonBooking.model.request.BookingStylits;
import com.example.hairSalonBooking.model.response.StylistForBooking;
import com.example.hairSalonBooking.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        // lấy được tất cả booking trong ngày của stylist đc truyền vào
        List<Booking> allBookingInDay = bookingRepository.getBookingsByStylistInDay(bookingSlots.getDate(),bookingSlots.getAccountId());
        // lấy ra tất cả các slot có trong database
        List<Slot> allSlot = slotRepository.findAll();
        List<Slot> slotToRemove = new ArrayList<>();
        // nếu stylist đó chưa có booking nào trong ngày
        if(allBookingInDay.isEmpty()){
            for (Slot slot : allSlot){
                // duyệt qua từng slot xét xem coi thời gian thực có qua thời gian của slot đó chưa
                LocalTime localTime = LocalTime.now();
                LocalDate date = LocalDate.now();
                if(date.isEqual(bookingSlots.getDate())){
                    // nếu thời gian thực qua thời gian của slot đó r thì add slot đó vào 1 cái list slotToRemove
                    if(localTime.isAfter(slot.getSlottime())){
                        slotToRemove.add(slot);
                    }
                }
            }
            // xóa tất cả thằng có trong slotToRemove
            allSlot.removeAll(slotToRemove);
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
            LocalTime time = slot.getSlottime().minusHours(totalTimeServiceNewBooking.getHour())
                        .minusMinutes(totalTimeServiceNewBooking.getMinute());
            // tìm ra list chứa các slot ko thỏa và add vào list slotToRemove
            List<Slot> list = slotRepository.getSlotToRemove(time,totalTimeServiceNewBooking.getHour());
            slotToRemove.addAll(list);
            slotToRemove.add(slot);// 10 11
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
}
