package com.example.hairSalonBooking.service;

import com.example.hairSalonBooking.model.request.MailBody;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

//    private final JavaMailSender javaMailSender;

//    public EmailService(JavaMailSender javaMailSender) {
//        this.javaMailSender = javaMailSender;
//    }
//
//
//    public void sendSimpleMessage(MailBody mailBody){
//    SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//    simpleMailMessage.setFrom("fsalon391@gmail.com");
//    simpleMailMessage.setTo(mailBody.getTo());
//    simpleMailMessage.setSubject(mailBody.getSubject());
//    simpleMailMessage.setText(mailBody.getText());
//
//    javaMailSender.send(simpleMailMessage);
//    }
}
