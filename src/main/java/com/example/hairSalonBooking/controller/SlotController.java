package com.example.hairSalonBooking.controller;


import com.example.hairSalonBooking.entity.Slot;
//import com.example.hairSalonBooking.service.SlotService;
import com.example.hairSalonBooking.service.SlotService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/slot") // giảm bớt đường dẫn
@CrossOrigin(origins = "http://localhost:3000")
public class SlotController {

    @Autowired
    SlotService slotService;

    @PostMapping("/create")
    public ResponseEntity createSlot(@Valid @RequestBody Slot slot) {
        Slot newSlot = slotService.create(slot);
        return ResponseEntity.ok(newSlot);
    }

    @GetMapping("/read")
    public ResponseEntity getAllSlot() {
        List<Slot> slots = slotService.getAllSlot();
        return ResponseEntity.ok(slots);
    }

    @PutMapping("{slotid}")
    public ResponseEntity updateSlot(@PathVariable long slotid,@Valid @RequestBody Slot slot) {

        Slot updateStudent = slotService.update(slotid, slot);
        return ResponseEntity.ok(updateStudent);

    }
    @DeleteMapping("{slotid}")
    public ResponseEntity deleteSlot(@PathVariable long slotid ) {
        Slot deleteStudent = slotService.delete(slotid);
        return ResponseEntity.ok(deleteStudent);
    }
}
