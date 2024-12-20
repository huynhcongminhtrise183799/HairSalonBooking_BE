package com.example.hairSalonBooking.service;

import com.example.hairSalonBooking.entity.*;
import com.example.hairSalonBooking.enums.BookingStatus;
import com.example.hairSalonBooking.enums.Role;
import com.example.hairSalonBooking.exception.AppException;
import com.example.hairSalonBooking.exception.ErrorCode;
import com.example.hairSalonBooking.model.request.*;
import com.example.hairSalonBooking.model.response.StylistForBooking;
import com.example.hairSalonBooking.repository.*;


import com.example.hairSalonBooking.model.response.*;

import com.google.firebase.database.core.view.Change;
import io.grpc.internal.ServiceConfigUtil;
import lombok.extern.slf4j.Slf4j;

import org.hibernate.sql.Update;
import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.sql.Date;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import java.util.*;

import java.util.Collections;


import java.util.Collections;
import java.util.stream.Collectors;


@Service
@Slf4j
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
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private StylistService stylistService;
    @Autowired
    private StylistScheduleService stylistScheduleService;
    @Autowired
    private EmailService emailService;
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
            response.setFeedbackScore(stylistService.calculateAverageFeedback(account.getAccountid(),"2024-10"));
            response.setId(account.getAccountid());
            response.setFullname(account.getFullname());
            response.setImage(account.getImage());
            stylistForBookings.add(response);
        }
        return stylistForBookings;
    }

    public List<Slot> getListSlot(BookingSlots bookingSlots){
        List<Slot> allSlot = slotRepository.getAllSlotActive();
        List<Slot> slotToRemove = new ArrayList<>();
        List<Shift> shifts = new ArrayList<>();
        List<Shift> shiftsFromSpecificStylistSchedule = shiftRepository.getShiftsFromSpecificStylistSchedule(bookingSlots.getAccountId(),bookingSlots.getDate());
        List<Shift> shiftMissingInSpecificStylistSchedule = shiftMissingInSpecificStylistSchedule(shiftsFromSpecificStylistSchedule);
        // stylist đã có booking trong ngày
        // tính tổng thời gian để hoàn thành yêu cầu booking mới
        LocalTime totalTimeServiceNewBooking = totalTimeServiceBooking(bookingSlots.getServiceId());
        slotToRemove.addAll(getSlotsExperiedTime(totalTimeServiceNewBooking,shiftsFromSpecificStylistSchedule));
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
            }else{
                break;
            }
        }
        // nếu stylist đó chưa có booking nào trong ngày
        if(allBookingInDay.isEmpty()){
            // xóa tất cả thằng có trong slotToRemove
            allSlot.removeAll(slotToRemove);
            return allSlot;
        }


        // duyệt qua từng booking có trong list allBookingInDay
        for(Booking booking : allBookingInDay){
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
                List<Slot> list = slotRepository.getSlotToRemove(slot.getSlottime(),TimeFinishBooking);
                slotToRemove.addAll(list); // 9

            }
            // tính ra thời gian
            // vd: slot 10h có ng đặt r, tổng thời gian service cho booking mới là 1h30p
            // tối thiểu phải là 8h30p mới đc đặt mà do slot ko có 8h30p
            // thì có nghĩa là 8h thỏa thời gian booking này và slot 9h ko thỏa
            LocalTime minimunTimeToBooking = slot.getSlottime().minusHours(totalTimeServiceNewBooking.getHour())
                    .minusMinutes(totalTimeServiceNewBooking.getMinute());
            // tìm ra list chứa các slot ko thỏa và add vào list slotToRemove
            List<Slot> list = slotRepository.getSlotToRemove(minimunTimeToBooking,TimeFinishBooking);
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
            // đếm xem có bao nhiêu booking complete trong ca làm đó
            int countTotalBookingCompleteInShift = bookingRepository.countTotalBookingCompleteInShift(shift.getShiftId(),bookingSlots.getAccountId(),bookingSlots.getDate());
            // nếu có đủ số lượng booking complete với limitBooking mà còn dư slot vẫn hiện ra
            if(countTotalBookingCompleteInShift == shift.getLimitBooking()){
                break;
            }
            // Tìm ra đc các slots thuộc về ca làm đó
            List<Slot> slots = slotRepository.getSlotsInShift(shift.getShiftId());
            // add list vừa tìm đc vào slotToRemove
            slotToRemove.addAll(slots);
        }
        allSlot.removeAll(slotToRemove);
        return allSlot;
    }
    public List<Slot> getSlotsUpdateByCustomer(BookingSlots bookingSlots, long bookingId){
        List<Slot> allSlot = slotRepository.getAllSlotActive();
        List<Slot> slotToRemove = new ArrayList<>();
        List<Shift> shifts = new ArrayList<>();
        List<Shift> shiftsFromSpecificStylistSchedule = shiftRepository.getShiftsFromSpecificStylistSchedule(bookingSlots.getAccountId(),bookingSlots.getDate());
        List<Shift> shiftMissingInSpecificStylistSchedule = shiftMissingInSpecificStylistSchedule(shiftsFromSpecificStylistSchedule);
        // stylist đã có booking trong ngày
        // tính tổng thời gian để hoàn thành yêu cầu booking mới
        LocalTime totalTimeServiceNewBooking = totalTimeServiceBooking(bookingSlots.getServiceId());
        slotToRemove.addAll(getSlotsExperiedTime(totalTimeServiceNewBooking,shiftsFromSpecificStylistSchedule));
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
        List<Booking> allBookingInDay = bookingRepository.getBookingsByStylistInDayForUpdate(bookingSlots.getDate(),bookingSlots.getAccountId(),bookingId);
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
            }else{
                break;
            }
        }
        // nếu stylist đó chưa có booking nào trong ngày
        if(allBookingInDay.isEmpty()){
            // xóa tất cả thằng có trong slotToRemove
            allSlot.removeAll(slotToRemove);
            return allSlot;
        }

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
//                if(totalTimeServiceForBooking.getMinute() == 0 ){
//                    List<Slot> list = slotRepository.getSlotToRemove(slot.getSlottime(),totalTimeServiceForBooking.getHour() - 1 );
//                    slotToRemove.addAll(list); // 9
//                }else{
//                    List<Slot> list = slotRepository.getSlotToRemove(slot.getSlottime(),totalTimeServiceForBooking.getHour());
//                    slotToRemove.addAll(list); // 9
//                }
                List<Slot> list = slotRepository.getSlotToRemove(slot.getSlottime(),TimeFinishBooking );
                slotToRemove.addAll(list);
            }
            // tính ra thời gian
            // vd: slot 10h có ng đặt r, tổng thời gian service cho booking mới là 1h30p
            // tối thiểu phải là 8h30p mới đc đặt mà do slot ko có 8h30p
            // thì có nghĩa là 8h thỏa thời gian booking này và slot 9h ko thỏa
            LocalTime minimunTimeToBooking = slot.getSlottime().minusHours(totalTimeServiceNewBooking.getHour())
                    .minusMinutes(totalTimeServiceNewBooking.getMinute());
            // tìm ra list chứa các slot ko thỏa và add vào list slotToRemove
            List<Slot> list = slotRepository.getSlotToRemove(minimunTimeToBooking,TimeFinishBooking);
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
            // đếm xem có bao nhiêu booking complete trong ca làm đó
            int countTotalBookingCompleteInShift = bookingRepository.countTotalBookingCompleteInShift(shift.getShiftId(),bookingSlots.getAccountId(),bookingSlots.getDate());
            // nếu có đủ số lượng booking complete với limitBooking mà còn dư slot vẫn hiện ra
            if(countTotalBookingCompleteInShift == shift.getLimitBooking()){
                break;
            }
            // Tìm ra đc các slots thuộc về ca làm đó
            List<Slot> slots = slotRepository.getSlotsInShift(shift.getShiftId());
            // add list vừa tìm đc vào slotToRemove
            slotToRemove.addAll(slots);
        }
        allSlot.removeAll(slotToRemove);
        return allSlot;
    }

    private Set<StylistForBooking> getStylistsBySkillAndDateWorkingAndShift(AssignNewStylistForBooking newStylistForBooking){
        Set<SalonService> services = new HashSet<>();
        for(Long id : newStylistForBooking.getServiceId()){
            SalonService service = serviceRepository.getServiceById(id);
            services.add(service);
        }
        Set<Skill> skills = new HashSet<>();
        for(SalonService service : services){
            Skill skill = skillRepository.findSkillBySkillId(service.getSkill().getSkillId());
            skills.add(skill);
        }
        Shift shift = shiftRepository.getShiftBySlot(newStylistForBooking.getSlotId());
        Set<Account> stylists = new HashSet<>();
        for(Skill skill : skills){
            Set<Account> accounts = accountRepository.getStylistForBooking(newStylistForBooking.getDate(),shift.getShiftId(),skill.getSkillId(), newStylistForBooking.getSalonId());
            if(stylists.isEmpty()){
                stylists.addAll(accounts);
            }else{
                stylists.retainAll(accounts);
            }
        }
        Set<StylistForBooking> stylistForBookings = new HashSet<>();
        for(Account account : stylists){
            StylistForBooking stylist = new StylistForBooking();
            stylist.setId(account.getAccountid());
            stylist.setFullname(account.getFullname());
            stylist.setImage(account.getImage());
            stylist.setFeedbackScore(stylistService.calculateAverageFeedback(account.getAccountid(),"2024-10"));
            stylistForBookings.add(stylist);
        }
        return stylistForBookings;
    }

    public Set<StylistForBooking> getStylistWhenUpdateBookingByManager(AssignNewStylistForBooking newStylistForBooking){

        // lay dc tat ca stylist co the lam list service do theo ca lam va ngay lam
        Set<StylistForBooking> stylistForBookings = getStylistsBySkillAndDateWorkingAndShift(newStylistForBooking);
        // tìm đc slot
        Slot slotBookingUpdate = slotRepository.findSlotBySlotid(newStylistForBooking.getSlotId());
        // tính tổng thời gian hoàn thành các services của booking mới
        LocalTime totalServiceTimeForNewBooking = totalTimeServiceBooking(newStylistForBooking.getServiceId());
        // tạo ra 1 list dùng để remove
        List<StylistForBooking> stylistsToRemove = new ArrayList<>();
        for(StylistForBooking stylist : stylistForBookings){
            // lay ra tat ca booking co trong ngay cua stylist do
            List<Booking> bookings = bookingRepository.getBookingsByStylistInDay(newStylistForBooking.getDate(), stylist.getId());
            // tìm đc booking gần nhất với thời gian của khách hàng muốn đổi
            Booking bookingNearestOverTime = bookingRepository.bookingNearestOverTime(stylist.getId(),slotBookingUpdate.getSlottime(),newStylistForBooking.getDate());
            Booking bookingNearestBeforeTime = bookingRepository.bookingNearestBeforeTime(stylist.getId(),slotBookingUpdate.getSlottime(),newStylistForBooking.getDate());
            Booking bookingAtTimeUpdate = bookingRepository.bookingAtTime(slotBookingUpdate.getSlotid(),stylist.getId(),newStylistForBooking.getDate());
            // tính đc thời gian hoàn thành booking mới đó
            LocalTime timeToCheckValid = slotBookingUpdate.getSlottime().plusHours(totalServiceTimeForNewBooking.getHour())
                    .plusMinutes(totalServiceTimeForNewBooking.getMinute());
            if(bookingAtTimeUpdate != null){
                stylistsToRemove.add(stylist);
            }
            if(bookingNearestOverTime != null){
                // lấy đc thời gian của cái booking có sẵn của stylist đó
                Slot slotTimeBooking = slotRepository.findSlotBySlotid(bookingNearestOverTime.getSlot().getSlotid());
                // nếu tổng thời gian hoàn thành booking mới đó mà lố thời gian của booking có sẵn thì stylist đó ko thỏa
                if(timeToCheckValid.isAfter(slotTimeBooking.getSlottime())){
                    stylistsToRemove.add(stylist);
                }
            }
            if(bookingNearestBeforeTime != null){
                LocalTime totalTimeServiceForBooking = serviceRepository.getTotalTime(bookingNearestBeforeTime.getBookingId());
                // lấy đc thời gian của cái booking có sẵn của stylist đó
                Slot slotTimeBooking = slotRepository.findSlotBySlotid(bookingNearestBeforeTime.getSlot().getSlotid());
                LocalTime totalTimeFinishBooking = slotTimeBooking.getSlottime().plusHours(totalTimeServiceForBooking.getHour())
                        .plusMinutes(totalTimeServiceForBooking.getMinute());

                // nếu tổng thời gian hoàn thành booking mới đó mà lố thời gian của booking có sẵn thì stylist đó ko thỏa
                if(totalTimeFinishBooking.isAfter(slotBookingUpdate.getSlottime())){
                    stylistsToRemove.add(stylist);
                }
            }
            if(!bookings.isEmpty()){
                boolean checkStylist = shiftsHaveFullBooking(bookings,slotBookingUpdate);
                if(checkStylist){
                    stylistsToRemove.add(stylist);
                }
            }
        }

        stylistForBookings.removeAll(stylistsToRemove);
        return stylistForBookings;
    }
    public BookingRequest createNewBooking(BookingRequest request){
        Account account = accountRepository.findAccountByAccountid(request.getCustomerId());
        Set<SalonService> services = new HashSet<>();
        for(Long id : request.getServiceId()){
            SalonService service = serviceRepository.getServiceById(id);
            services.add(service);
        }
        Slot slot = slotRepository.findSlotBySlotid(request.getSlotId());
        BookingSlots bookingSlots = new BookingSlots();
        bookingSlots.setServiceId(request.getServiceId());
        bookingSlots.setDate(request.getBookingDate());
        bookingSlots.setSalonId(request.getSalonId());
        bookingSlots.setAccountId(request.getStylistId());
        List<Slot> slotAvailable = getListSlot(bookingSlots);
        int count = 0 ;
        for(Slot s : slotAvailable){
            if(s.getSlotid() == request.getSlotId()){
                count++;
            }
        }
        if(count == 0){
            throw new AppException(ErrorCode.SLOT_NOT_VALID);

        }
        SalonBranch salonBranch = salonBranchRepository.findSalonBranchBySalonId(request.getSalonId());
        Voucher voucher = voucherRepository.findVoucherByVoucherId(request.getVoucherId());
        if(voucher != null){
            voucher.setQuantity(voucher.getQuantity() - 1 );
        }
        StylistSchedule stylistSchedule = stylistScheduleRepository.getScheduleId(request.getStylistId(), request.getBookingDate());
        Booking checkBookingExist = bookingRepository.getBySlotSlotidAndBookingDayAndStylistScheduleStylistScheduleId(slot.getSlotid(),request.getBookingDate(),stylistSchedule.getStylistScheduleId());
        if(checkBookingExist != null){
            throw new AppException(ErrorCode.BOOKING_EXIST);
        }
        Booking booking = new Booking();
        booking.setBookingDay(request.getBookingDate());
        booking.setAccount(account);
        booking.setSlot(slot);
        booking.setSalonBranch(salonBranch);
        booking.setServices(services);
        booking.setVoucher(voucher);
        booking.setStylistSchedule(stylistSchedule);
        booking.setStatus(BookingStatus.PENDING);
        Booking newBooking = bookingRepository.save(booking);
        for(SalonService service : services){
            bookingRepository.updateBookingDetail(service.getPrice(),newBooking.getBookingId(),service.getServiceId());
        }
        Account currentAccount = currentAccount();
        if(currentAccount.getRole().equals(Role.CUSTOMER)){
            CreateNewBookingSuccess success = new CreateNewBookingSuccess();
            success.setDate(booking.getBookingDay());
            success.setTime(booking.getSlot().getSlottime());
            success.setTo(currentAccount().getEmail());
            success.setSubject("Create booking successfully");
            success.setStylistName(booking.getStylistSchedule().getAccount().getFullname());
            success.setSalonAddress(booking.getSalonBranch().getAddress());

            emailService.sendMailInformBookingSuccess(success);
        }
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
        BookingSlots bookingSlots = new BookingSlots();
        bookingSlots.setServiceId(request.getServiceId());
        bookingSlots.setDate(request.getBookingDate());
        bookingSlots.setSalonId(request.getSalonId());
        bookingSlots.setAccountId(request.getStylistId());
        List<Slot> slotAvailable = getListSlot(bookingSlots);
        int count = 0 ;
        for(Slot s : slotAvailable){
            if(s.getSlotid() == request.getSlotId()){
                count++;
            }
        }
        if(count == 0){
            throw new AppException(ErrorCode.SLOT_NOT_VALID);

        }
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
        for(SalonService service : services){
            bookingRepository.updateBookingDetail(service.getPrice(),booking.getBookingId(),service.getServiceId());
        }
        Booking bookingToRemove = new Booking();
        if(!stylistScheduleService.bookingByShiftNotWorking.isEmpty()){
            for(Booking booking1 : stylistScheduleService.bookingByShiftNotWorking){
                if(booking.getBookingId() == booking1.getBookingId()){
                    bookingToRemove = booking1;
                    break;
                }
            }
        }
        stylistScheduleService.bookingByShiftNotWorking.remove(bookingToRemove);
        Account currentAccount = currentAccount();
        if(currentAccount.getRole().equals(Role.BRANCH_MANAGER)){
            ChangeStylist success = new ChangeStylist();
            success.setDate(booking.getBookingDay());
            success.setTime(booking.getSlot().getSlottime());
            success.setTo(booking.getAccount().getEmail());
            success.setSubject("Change Stylist");
            success.setStylistName(booking.getStylistSchedule().getAccount().getFullname());
            success.setSalonAddress(booking.getSalonBranch().getAddress());
            emailService.sendMailChangeStylist(success);
            System.out.println("Send thanh cong o service");
        }
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
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        return "booking deleted";
    }
    public BookingResponse getBookingById(long bookingId){
        Booking booking = bookingRepository.findBookingByBookingId(bookingId);
        //Set<String> serviceName = serviceRepository.getServiceNameByBooking(bookingId);
        Set<SalonService> services = serviceRepository.getServiceForBooking(bookingId);
        Set<Long> serviceId = new HashSet<>();
        for(SalonService service : services){
            serviceId.add(service.getServiceId());
        }
        Account account = accountRepository.findAccountByAccountid(booking.getStylistSchedule().getAccount().getAccountid());
        BookingResponse bookingResponse = new BookingResponse();
        bookingResponse.setId(booking.getBookingId());
        bookingResponse.setDate(booking.getBookingDay());
        bookingResponse.setTime(booking.getSlot().getSlottime());
        bookingResponse.setCustomerId(booking.getAccount().getAccountid());
        bookingResponse.setCustomerName(booking.getAccount().getFullname());
        bookingResponse.setSalonName(booking.getSalonBranch().getAddress());
        bookingResponse.setServiceId(serviceId);

        bookingResponse.setStylistName(account.getFullname());
        if(booking.getVoucher() != null){
            bookingResponse.setVoucherCode(booking.getVoucher().getCode());
        }
        return bookingResponse;

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

    public boolean shiftsHaveFullBooking(List<Booking> bookings,Slot slotBookingUpdate){
        List<Shift> shifts = new ArrayList<>();
        // duyet qua tat ca booking trong ngay cua stylist do
        for(Booking booking : bookings){
            Slot slot = slotRepository.findSlotBySlotid(booking.getSlot().getSlotid());
            LocalTime totalTimeServiceForBooking = serviceRepository.getTotalTime(booking.getBookingId());
            LocalTime timeFinishBooking = slot.getSlottime().plusHours(totalTimeServiceForBooking.getHour())
                    .plusMinutes(totalTimeServiceForBooking.getMinute());
            List<Shift> shiftBookingBelongTo = shiftRepository.getShiftForBooking(slot.getSlottime(),timeFinishBooking,booking.getBookingId());
            shifts.addAll(shiftBookingBelongTo);
        }
        List<Shift> shiftReachedBookingLimit = shiftReachedBookingLimit(shifts);
        for(Shift shift : shiftReachedBookingLimit){
            Shift shiftBySlot = shiftRepository.getShiftBySlot(slotBookingUpdate.getSlotid());
            if(shift.getShiftId() == shiftBySlot.getShiftId()){
                return true;
            }
        }
        return false;
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
    private List<Slot> getSlotsExperiedTime(LocalTime time,List<Shift> shifts){
        Shift shift = shifts.get(0);
        List<Slot> slotsToRemove = new ArrayList<>();

        List<Slot> slots = slotRepository.getSlotsInShift(shift.getShiftId());
        for(Slot slot : slots){
            LocalTime totalTime = slot.getSlottime().plusHours(time.getHour())
                    .plusMinutes(time.getMinute());
            if (totalTime.isBefore(slot.getSlottime())) {
                // Thời gian totalTime đã vượt qua ngày mới
                totalTime = totalTime.plusHours(24); // Chuyển totalTime sang ngày mới
            }
            if(totalTime.isAfter(shift.getEndTime()) || totalTime.isBefore(slot.getSlottime())){
                slotsToRemove.add(slot);
            }
        }
        return slotsToRemove;
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
                    //response.setVoucherCode(booking.getVoucher() != null ? booking.getVoucher().getCode() : null);
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
    public PaymentResponse finishedService(long bookingId) {

        Booking booking = bookingRepository.findBookingByBookingId(bookingId);
        if (booking == null) {
            throw new AppException(ErrorCode.BOOKING_NOT_FOUND);
        }

        Set<SalonService> services = booking.getServices();
        double totalAmount = 0;
        Set<PaymentServiceResponse> serviceResponses = new HashSet<>();
        for (SalonService service : services) {
            totalAmount += service.getPrice();
            serviceResponses.add(new PaymentServiceResponse(service.getServiceName(), service.getImage(),service.getPrice()));
        }
        String voucherCode = null;
        if (booking.getVoucher() != null) {
            double discount = booking.getVoucher().getDiscountAmount();
            totalAmount -= totalAmount * discount / 100;
            voucherCode = booking.getVoucher().getCode();
        }
        Payment existingPayment = paymentRepository.findPaymentByBooking(booking);
        if (existingPayment != null) {
            // Delete the existing payment
            paymentRepository.delete(existingPayment);
        }
        String stylistName = booking.getStylistSchedule().getAccount().getFullname();


        Payment payment = Payment.builder()
                .paymentAmount(totalAmount)
                .paymentDate(LocalDate.now())
//                .paymentMethod("VNPay-Banking")   // Hoặc phương thức thanh toán khác
                .paymentStatus("Pending") // Trạng thái ban đầu
                .booking(booking)         // Liên kết với Booking
                .build();
        paymentRepository.save(payment);
        return new PaymentResponse(
                booking.getBookingId(),
                booking.getBookingDay(),
                booking.getAccount().getFullname(),
                stylistName,
                booking.getSalonBranch().getAddress(),
                serviceResponses,
                voucherCode,
                totalAmount

        );
    }
    public String checkout(String transactionId, Long bookingId) {
        Payment payment = null;
        Booking booking = null;
        if (transactionId != null && !transactionId.isEmpty()) {
            payment = paymentRepository.findByTransactionId(transactionId);
            if (payment == null) {
                throw new AppException(ErrorCode.BOOKING_NOT_FOUND);
            }
            booking = payment.getBooking();
        }

        // Check if bookingId is provided
        else if (bookingId != null) {
            booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));
            payment = booking.getPayment();
        } else {
            throw new AppException(ErrorCode.EXCEPTION);
        }

        // Update booking status
        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);

        // Update payment status
        booking.getPayment().setPaymentStatus("Completed");
        if(booking.getPayment().getPaymentMethod() == null){
            booking.getPayment().setPaymentMethod("Cash");
            booking.getPayment().setTransactionId(null);
        }
        paymentRepository.save(payment);

        return "Check-out success";
    }

    // Hàm update logic về quản lí thời gian booking
    public BookingRequest updateBookingWithService(Long bookingId, Set<Long> newServiceIds) {
        log.info("Booking ID: {}", bookingId);
        log.info("New Service IDs: {}", newServiceIds);


        // tìm id booking
        Booking booking = bookingRepository.findBookingById(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));


        // log xem booking lấy đúng ko
        log.info("Found booking: {}", booking);
        // Lấy danh sách service hiện tại của booking
        Set<Long> currentServiceIds = booking.getServices().stream()
                .map(SalonService::getServiceId)
                .collect(Collectors.toSet());

        // Kiểm tra nếu dịch vụ cũ trùng với dịch vụ mới
        if (currentServiceIds.equals(newServiceIds)) {
            throw new AppException(ErrorCode.SERVICES_ALREADY_BOOKED);
        }
        // tìm thằng booking tiếp theo
        Optional<Booking> nextBooking = bookingRepository.findNextBookingSameDay(
                booking.getStylistSchedule().getAccount().getAccountid(),
                booking.getSlot().getSlotid(),
                booking.getBookingDay()
        );
        log.info("Stylist Account ID: {}", booking.getStylistSchedule().getAccount().getAccountid());
        log.info("Current Slot Time: {}", booking.getSlot().getSlotid());
        log.info("Booking Day: {}", booking.getBookingDay());
        log.info("Next booking: {}", nextBooking);

        // tính tổng thời gian booking
        LocalTime currentTotalDuration = totalTimeServiceBooking(
                booking.getServices().stream()
                        .map(SalonService::getServiceId)
                        .collect(Collectors.toSet())
        );

        log.info(" Total Duration: {}", currentTotalDuration);
        // tính tổng thời gian khi thêm service mới
        LocalTime newServicesDuration = totalTimeServiceBooking(newServiceIds);
        log.info(" Total Duration: {}", newServicesDuration);


        // Kết hợp thời lượng của dịch vụ hiện tại và dịch vụ mới
        LocalTime newTotalDuration = currentTotalDuration
                .plusHours(newServicesDuration.getHour())
                .plusMinutes(newServicesDuration.getMinute());

        // Kiểm tra lịch đặt trước của stylist , xem coi có xung đột thời gian
        if (nextBooking.isPresent()) {
            LocalTime nextSlotTime = nextBooking.get().getSlot().getSlottime();
            LocalTime currentSlotTime = booking.getSlot().getSlottime();

            // tính thời gian còn trống cho đến lần đặt booking tiếp theo
            long availableTime = Duration.between(currentSlotTime, nextSlotTime).toMinutes();


            // Xem tổng thời gian vuọt qua thời gian khả dụng không nếu ko thì update ko thì thông báo stylist unvailable
            if (newTotalDuration.getHour() * 60 + newTotalDuration.getMinute() > availableTime) {
                throw new AppException(ErrorCode.STYLIST_UNAVAILABLE);
            }
        }

        booking.getServices().clear();

        // tìm kiếm servicé mới
        List<SalonService> newServices = serviceRepository.findByServiceIdIn(new ArrayList<>(newServiceIds));

        if (newServices.isEmpty()) {
            log.error("No services found with the provided IDs.");
            throw new AppException(ErrorCode.SERVICES_NOT_FOUND);
        }

        // sau khi tìm kiếm xong thì add vào
        booking.getServices().addAll(newServices);
        log.info("Final Booking before saving: {}", booking);

        // cuối cùng là lưu vào
        bookingRepository.save(booking);
        for(SalonService service : newServices){
            bookingRepository.updateBookingDetail(service.getPrice(),booking.getBookingId(),service.getServiceId());
        }
        // Tạo BookingRequest trả về
        BookingRequest response = new BookingRequest();
        response.setCustomerId(booking.getAccount().getAccountid());
        response.setSlotId(booking.getSlot().getSlotid());
        response.setSalonId(booking.getSalonBranch().getSalonId());
        // Set sau khi update Service
        response.setServiceId(
                booking.getServices().stream()
                        .map(SalonService::getServiceId)
                        .collect(Collectors.toSet())
        );
        response.setStylistId(booking.getStylistSchedule().getAccount().getAccountid());
        response.setBookingDate(booking.getBookingDay());
        // xử lí voucher đặt null nếu voucher ko tồn tại
        response.setVoucherId(
                booking.getVoucher() != null ? booking.getVoucher().getVoucherId() : 0
        );
        return response;
    }

    public List<TotalMoneyByBookingDay> totalMoneyByBookingDayInMonth(int month, long salonId){
        List<Object[]> objects = bookingRepository.getTotalMoneyByBookingDay(month, salonId);
        List<TotalMoneyByBookingDay> responses = new ArrayList<>();
        for(Object[] object : objects){
            LocalDate date = ((Date) object[0]).toLocalDate();
            double totalMoney = (double) object[1];
            TotalMoneyByBookingDay totalMoneyByBookingDay = new TotalMoneyByBookingDay(date,totalMoney);
            responses.add(totalMoneyByBookingDay);
        }
        return responses;
    }
    public TotalMoneyByBookingDay totalMoneyBySalonInMonth(int month, long salonId){
        double totalMoney = bookingRepository.getTotalMoneyBySalonIdInMonth(month,salonId);
        TotalMoneyByBookingDay responses = new TotalMoneyByBookingDay(null,totalMoney);
        return responses;
    }
    public double totalMoneyAllSalonByMonth(int month){
        return bookingRepository.getTotalMoneyAllSalonIdInMonth(month);
    }
    public Long countAllBookingsInMonth(int month){
        return bookingRepository.countAllBookingsInMonth(month);
    }
    public BookingPageResponse getAllBookingsForStylistInBranchByPending(int page, int size, Long branchId, LocalDate date) {
        // Check if the branch exists
        SalonBranch branch = salonBranchRepository.findById(branchId)
                .orElseThrow(() -> new IllegalArgumentException("Branch not found"));

        // Get all stylists in the branch
        List<Account> stylists = accountRepository.getStylistsBySalonId(branchId, Role.STYLIST);

        // Retrieve and filter bookings with PENDING status for each stylist in the branch
        List<BookingResponse> bookingResponses = stylists.stream()
                .flatMap(account -> bookingRepository.findAllByAccountInAndSalonBranch(account.getAccountid(), date).stream())
                .filter(booking -> booking.getStatus() == BookingStatus.PENDING)
                .map(booking -> {
                    Set<Long> serviceId = serviceRepository.getServiceIdByBooking(booking.getBookingId());

                    BookingResponse bookingResponse = new BookingResponse();
                    bookingResponse.setId(booking.getBookingId());
                    bookingResponse.setCustomerId(booking.getAccount().getAccountid());
                    bookingResponse.setStylistName(booking.getStylistSchedule().getAccount().getFullname());
                    bookingResponse.setTime(booking.getSlot().getSlottime());
                    bookingResponse.setDate(booking.getBookingDay());
                    bookingResponse.setSalonName(booking.getSalonBranch().getAddress());
                    bookingResponse.setServiceId(serviceId);
                    bookingResponse.setStatus(booking.getStatus());
                    bookingResponse.setCustomerName(booking.getAccount().getFullname());

                    if (booking.getVoucher() != null) {
                        bookingResponse.setVoucherCode(booking.getVoucher().getCode());
                    }

                    return bookingResponse;
                })
                .collect(Collectors.toList());

        // Create a Page object for bookings
        int totalBookings = bookingResponses.size();
        int start = Math.min(page * size, totalBookings);
        int end = Math.min(start + size, totalBookings);
        List<BookingResponse> pagedResponses = bookingResponses.subList(start, end);

        // Build the BookingPageResponse
        BookingPageResponse bookingPageResponse = new BookingPageResponse();
        bookingPageResponse.setPageNumber(page);
        bookingPageResponse.setTotalPages((int) Math.ceil((double) totalBookings / size));
        bookingPageResponse.setTotalElements(totalBookings);
        bookingPageResponse.setContent(pagedResponses);

        return bookingPageResponse;
    }
    public BookingPageResponse getAllBookingsForStylistInBranchByComplete(int page, int size, Long branchId, LocalDate date) {
        // Check if the branch exists
        SalonBranch branch = salonBranchRepository.findById(branchId)
                .orElseThrow(() -> new IllegalArgumentException("Branch not found"));

        // Get all stylists in the branch
        List<Account> stylists = accountRepository.getStylistsBySalonId(branchId, Role.STYLIST);

        // Retrieve and filter bookings with PENDING status for each stylist in the branch
        List<BookingResponse> bookingResponses = stylists.stream()
                .flatMap(account -> bookingRepository.findAllByAccountInAndSalonBranch(account.getAccountid(), date).stream())
                .filter(booking -> booking.getStatus() == BookingStatus.COMPLETED)
                .map(booking -> {
                    Set<Long> serviceId = serviceRepository.getServiceIdByBooking(booking.getBookingId());

                    BookingResponse bookingResponse = new BookingResponse();
                    bookingResponse.setId(booking.getBookingId());
                    bookingResponse.setCustomerId(booking.getAccount().getAccountid());
                    bookingResponse.setStylistName(booking.getStylistSchedule().getAccount().getFullname());
                    bookingResponse.setTime(booking.getSlot().getSlottime());
                    bookingResponse.setDate(booking.getBookingDay());
                    bookingResponse.setSalonName(booking.getSalonBranch().getAddress());
                    bookingResponse.setServiceId(serviceId);
                    bookingResponse.setStatus(booking.getStatus());
                    bookingResponse.setCustomerName(booking.getAccount().getFullname());

                    if (booking.getVoucher() != null) {
                        bookingResponse.setVoucherCode(booking.getVoucher().getCode());
                    }

                    return bookingResponse;
                })
                .collect(Collectors.toList());

        // Create a Page object for bookings
        int totalBookings = bookingResponses.size();
        int start = Math.min(page * size, totalBookings);
        int end = Math.min(start + size, totalBookings);
        List<BookingResponse> pagedResponses = bookingResponses.subList(start, end);

        // Build the BookingPageResponse
        BookingPageResponse bookingPageResponse = new BookingPageResponse();
        bookingPageResponse.setPageNumber(page);
        bookingPageResponse.setTotalPages((int) Math.ceil((double) totalBookings / size));
        bookingPageResponse.setTotalElements(totalBookings);
        bookingPageResponse.setContent(pagedResponses);

        return bookingPageResponse;
    }
    public BookingPageResponse getAllBookingsForStylistInBranchByInprocess(int page, int size, Long branchId, LocalDate date) {
        // Check if the branch exists
        SalonBranch branch = salonBranchRepository.findById(branchId)
                .orElseThrow(() -> new IllegalArgumentException("Branch not found"));

        // Get all stylists in the branch
        List<Account> stylists = accountRepository.getStylistsBySalonId(branchId, Role.STYLIST);

        // Retrieve and filter bookings with PENDING status for each stylist in the branch
        List<BookingResponse> bookingResponses = stylists.stream()
                .flatMap(account -> bookingRepository.findAllByAccountInAndSalonBranch(account.getAccountid(), date).stream())
                .filter(booking -> booking.getStatus() == BookingStatus.IN_PROGRESS)
                .map(booking -> {
                    Set<Long> serviceId = serviceRepository.getServiceIdByBooking(booking.getBookingId());

                    BookingResponse bookingResponse = new BookingResponse();
                    bookingResponse.setId(booking.getBookingId());
                    bookingResponse.setCustomerId(booking.getAccount().getAccountid());
                    bookingResponse.setStylistName(booking.getStylistSchedule().getAccount().getFullname());
                    bookingResponse.setTime(booking.getSlot().getSlottime());
                    bookingResponse.setDate(booking.getBookingDay());
                    bookingResponse.setSalonName(booking.getSalonBranch().getAddress());
                    bookingResponse.setServiceId(serviceId);
                    bookingResponse.setStatus(booking.getStatus());
                    bookingResponse.setCustomerName(booking.getAccount().getFullname());

                    if (booking.getVoucher() != null) {
                        bookingResponse.setVoucherCode(booking.getVoucher().getCode());
                    }

                    return bookingResponse;
                })
                .collect(Collectors.toList());

        // Create a Page object for bookings
        int totalBookings = bookingResponses.size();
        int start = Math.min(page * size, totalBookings);
        int end = Math.min(start + size, totalBookings);
        List<BookingResponse> pagedResponses = bookingResponses.subList(start, end);

        // Build the BookingPageResponse
        BookingPageResponse bookingPageResponse = new BookingPageResponse();
        bookingPageResponse.setPageNumber(page);
        bookingPageResponse.setTotalPages((int) Math.ceil((double) totalBookings / size));
        bookingPageResponse.setTotalElements(totalBookings);
        bookingPageResponse.setContent(pagedResponses);

        return bookingPageResponse;
    }
    public BookingPageResponse getAllBookingsForStylistInBranchByCancel(int page, int size, Long branchId, LocalDate date) {
        // Check if the branch exists
        SalonBranch branch = salonBranchRepository.findById(branchId)
                .orElseThrow(() -> new IllegalArgumentException("Branch not found"));

        // Get all stylists in the branch
        List<Account> stylists = accountRepository.getStylistsBySalonId(branchId, Role.STYLIST);

        // Retrieve and filter bookings with PENDING status for each stylist in the branch
        List<BookingResponse> bookingResponses = stylists.stream()
                .flatMap(account -> bookingRepository.findAllByAccountInAndSalonBranch(account.getAccountid(), date).stream())
                .filter(booking -> booking.getStatus() == BookingStatus.CANCELLED)
                .map(booking -> {
                    Set<Long> serviceId = serviceRepository.getServiceIdByBooking(booking.getBookingId());

                    BookingResponse bookingResponse = new BookingResponse();
                    bookingResponse.setId(booking.getBookingId());
                    bookingResponse.setCustomerId(booking.getAccount().getAccountid());
                    bookingResponse.setStylistName(booking.getStylistSchedule().getAccount().getFullname());
                    bookingResponse.setTime(booking.getSlot().getSlottime());
                    bookingResponse.setDate(booking.getBookingDay());
                    bookingResponse.setSalonName(booking.getSalonBranch().getAddress());
                    bookingResponse.setServiceId(serviceId);
                    bookingResponse.setStatus(booking.getStatus());
                    bookingResponse.setCustomerName(booking.getAccount().getFullname());

                    if (booking.getVoucher() != null) {
                        bookingResponse.setVoucherCode(booking.getVoucher().getCode());
                    }

                    return bookingResponse;
                })
                .collect(Collectors.toList());

        // Create a Page object for bookings
        int totalBookings = bookingResponses.size();
        int start = Math.min(page * size, totalBookings);
        int end = Math.min(start + size, totalBookings);
        List<BookingResponse> pagedResponses = bookingResponses.subList(start, end);

        // Build the BookingPageResponse
        BookingPageResponse bookingPageResponse = new BookingPageResponse();
        bookingPageResponse.setPageNumber(page);
        bookingPageResponse.setTotalPages((int) Math.ceil((double) totalBookings / size));
        bookingPageResponse.setTotalElements(totalBookings);
        bookingPageResponse.setContent(pagedResponses);

        return bookingPageResponse;
    }
    public String checkBookingStatus(long bookingId){
        Booking booking = bookingRepository.checkBookingStatus(bookingId);
        if(booking == null){
            return "false";
        }
        return "true";
    }

    public Long countAllBookingsCompleted(String yearAndMonth){
        String arr[] = yearAndMonth.split("-");
        int year = Integer.parseInt(arr[0]);
        int month = Integer.parseInt(arr[1]);

        return bookingRepository.countAllBookingsCompleted(year,month);
    }

    private Account currentAccount(){
        var context = SecurityContextHolder.getContext();
        Account account = (Account) context.getAuthentication().getPrincipal();
        return account;
    }
}