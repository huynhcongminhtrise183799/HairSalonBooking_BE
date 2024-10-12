package com.example.hairSalonBooking.controller;


import com.example.hairSalonBooking.entity.Booking;
import com.example.hairSalonBooking.entity.Payment;
import com.example.hairSalonBooking.enums.BookingStatus;
import com.example.hairSalonBooking.exception.AppException;
import com.example.hairSalonBooking.exception.ErrorCode;
import com.example.hairSalonBooking.model.response.ResponseObject;
import com.example.hairSalonBooking.repository.BookingRepository;
import com.example.hairSalonBooking.repository.PaymentRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.hairSalonBooking.config.VnpayConfig;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin("http://localhost:3000/")
//@CrossOrigin("http://localhost:8080/payment/response")
@SecurityRequirement(name = "api")
public class PaymentController {
    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @GetMapping("/Pay/{bookingId}")
    public String getPay(@PathVariable Long bookingId, HttpServletRequest req ) throws UnsupportedEncodingException {
        Booking booking = bookingRepository.findBookingByBookingId(bookingId);
        if(booking == null){
            throw new AppException(ErrorCode.BOOKING_NOT_FOUND);
        }
        Payment payment = paymentRepository.findPaymentByBooking(booking);
        if (payment == null || payment.getPaymentAmount() <= 0) {
            throw new AppException(ErrorCode.INVALID_PAYMENT_AMOUNT);
        }
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        long amount = (long) (payment.getPaymentAmount() * 100);
//        long amount = 10000;
        String bankCode = req.getParameter("bankCode");
//        String bankCode = "NCB";
//        if (bankCode == null || bankCode.isEmpty()) {
//            bankCode = "NCB";
//        }
        String vnp_TxnRef = VnpayConfig.getRandomNumber(8);
        //cap nhat ma giao dich
        payment.setTransactionId(vnp_TxnRef);
        paymentRepository.save(payment);
        String vnp_IpAddr = VnpayConfig.getIpAddress(req);
//        String vnp_IpAddr = "127.0.0.1";
        String vnp_TmnCode = VnpayConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");

        if (bankCode != null && !bankCode.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bankCode);
        }
//        vnp_Params.put("vnp_BankCode", bankCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
//        String locate = req.getParameter("language");
//        if (locate != null && !locate.isEmpty()) {
//            vnp_Params.put("vnp_Locale", locate);
//        } else {
//            vnp_Params.put("vnp_Locale", "vn");
//        }
        vnp_Params.put("vnp_ReturnUrl", VnpayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VnpayConfig.hmacSHA512(VnpayConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VnpayConfig.vnp_PayUrl + "?" + queryUrl;
        log.info("VNPay Request Params: " + vnp_Params);
        log.info("VNPay Payment URL: " + paymentUrl);

        return paymentUrl;
    }
    @GetMapping("/payment/response")
    public ResponseEntity<String> checkPaymentSuccess(@RequestParam Map<String, String> vnp_Params) {
        String vnp_ResponseCode = vnp_Params.get("vnp_ResponseCode");

        // Kiểm tra mã phản hồi từ VNPay
        if ("00".equals(vnp_ResponseCode)) {
            // Giao dịch thành công
            return ResponseEntity.ok("Payment success. Transaction ID: " + vnp_Params.get("vnp_TxnRef"));
        } else {
            // Giao dịch không thành công
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment failed with code: " + vnp_ResponseCode);
        }
    }

}
