package com.example.hairSalonBooking.service;

import com.example.hairSalonBooking.entity.Booking;
import com.example.hairSalonBooking.entity.SalonService;
import com.example.hairSalonBooking.entity.Slot;
import com.example.hairSalonBooking.exception.AppException;
import com.example.hairSalonBooking.exception.ErrorCode;
import com.example.hairSalonBooking.repository.BookingRepository;
import com.example.hairSalonBooking.repository.ServiceRepository;
import com.example.hairSalonBooking.repository.SlotRepository;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class SlotService {
    @Autowired
    SlotRepository slotRepository;
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    ServiceRepository serviceRepository;
//
//    public Slot create(Slot slot) {
//        try {
//            Slot newSlot = slotRepository.save(slot);
//        }catch(Exception e) {
//            if(e.getMessage().contains(slot.getSlotid())) {
//                throw new AppException(ErrorCode.USERNAME_EXISTED);
//            }else {
//                throw new AppException(ErrorCode.Phone_EXISTED);
//            }
//        }
//        return newSlot;
//    }
    public Slot create(Slot slot) {
        // Kiểm tra xem slot có ID đã tồn tại không
        if (slot.getSlotid() != 0 && slotRepository.existsById(slot.getSlotid())) {
            throw new AppException(ErrorCode.SLOT_ID_EXISTED); // Ném ngoại lệ nếu ID đã tồn tại
        }

        // Lưu slot mới
        return slotRepository.save(slot);
    }

    //Read
    public List<Slot> getAllSlot() {
        List<Slot> slots = slotRepository.findAll();
        return slots;
    }

    //Update
    public Slot update(long slotid, Slot slot) {
        //B1: tìm ra thằng student cần đc update thông qua ID
        Slot updeSlot = slotRepository.findSlotBySlotid(slotid);
        if (updeSlot == null) {
            // Handle the case when the stylist is not found
            throw new AppException(ErrorCode.SLOT_NOT_FOUND);
        }
        //B2: Cập nhập thông tin nó
        updeSlot.setSlotid(slot.getSlotid());
        updeSlot.setSlottime(slot.getSlottime());
        //B3: Lưu xuống DataBase
        return slotRepository.save(updeSlot);

    }

//Delete

public Slot delete(long slotid) {
    //B1: tìm ra thằng student cần đc update thông qua ID
    Slot updeSlot = slotRepository.findSlotBySlotid(slotid);
    if (updeSlot == null) {
        // Handle the case when the stylist is not found
        throw new AppException(ErrorCode.SLOT_NOT_FOUND);
    }
    updeSlot.setDeleted(true);
    return slotRepository.save(updeSlot);
}

    /*public List<Slot> getSlotsForBooking(LocalDate date, long stylistId){
        // Tìm tất cả các booking có trong ngày của stylist đó
        List<Booking> bookings = bookingRepository.getBookingsByStylistInDay(date,stylistId);
        // nếu chưa có booking nào => hiện full slot
        if(bookings.isEmpty()){
            return slotRepository.findAll();
        }
        List<Slot> slots = new ArrayList<>();
        List<Slot> slotForRemove = new ArrayList<>();
        for(Booking booking : bookings){
            LocalTime totalTime = serviceRepository.getTotalTime(booking.getBookingId());
            Slot slot = slotRepository.findSlotBySlotid(booking.getSlot().getSlotid());
            LocalTime newTime = slot.getSlottime().plusHours(totalTime.getHour())
                    .plusMinutes(totalTime.getMinute());
            List<Slot> slotCanBooking = slotRepository.getAllSlotCanBooking(newTime);
            slots.addAll(slotCanBooking);
            slotForRemove.add(slot);
        }
        slots.removeAll(slotForRemove);
        //if
        // Tìm ra booking mới nhất trong ngày của stylist đó
        /*Booking bookingNearest = bookingRepository.bookingNearest(date,stylistId);
        System.out.println(bookingNearest.getBookingId());
        // Tính tổng thời gian dự kiến làm cho booking mới nhất đó
        LocalTime totalTime = serviceRepository.getTotalTime(bookingNearest.getBookingId());
        System.out.println(totalTime);
        // Lấy ra được thời gian của slot trong booking mới nhất
        Slot slot = slotRepository.findSlotBySlotid(bookingNearest.getSlot().getSlotid());
        System.out.println(slot.getSlotid());
        // cập nhật giờ mới
        LocalTime newTime = slot.getSlottime().plusHours(totalTime.getHour())
                        .plusMinutes(totalTime.getMinute());
        System.out.println(newTime);
        // lấy ra những slot còn trống
        List<Slot> slotCanBooking = slotRepository.getAllSlotCanBooking(newTime);*/
        //return slots;
    //}

    public LocalTime test(LocalDate date, long stylistId){
        Booking bookingNearest = bookingRepository.bookingNearest(date,stylistId);
        System.out.println(bookingNearest.getBookingId());
        return serviceRepository.getTotalTime(bookingNearest.getBookingId());
    }

    /*private int checkLimitBooking(LocalDate date, long stylistId){

    }*/
}
