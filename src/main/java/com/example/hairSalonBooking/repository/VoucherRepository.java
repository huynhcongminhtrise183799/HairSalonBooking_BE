package com.example.hairSalonBooking.repository;

import com.example.hairSalonBooking.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoucherRepository extends JpaRepository<Voucher,Long> {
    Voucher findVoucherByCode(String code);
    Voucher findVoucherByVoucherId(long id);
    Voucher findVoucherByCodeAndIsDeleteFalse(String code);
    List<Voucher> findVouchersByIsDeleteFalse();
}
