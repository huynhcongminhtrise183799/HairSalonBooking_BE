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

}
