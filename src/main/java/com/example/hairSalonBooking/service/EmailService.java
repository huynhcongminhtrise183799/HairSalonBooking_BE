package com.example.hairSalonBooking.service;

import com.example.hairSalonBooking.model.request.MailBody;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Service
public class EmailService {
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendSimpleMessage(MailBody mailBody){
        try {
            Context context = new Context();
            context.setVariable("name",mailBody.getTo());
            context.setVariable("otp",mailBody.getOtp());
            String template = templateEngine.process("OTP-ForgotPassword",context);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            mimeMessageHelper.setFrom("fsalon391@gmail.com");
            mimeMessageHelper.setTo(mailBody.getTo());
            mimeMessageHelper.setText(template,true);
            mimeMessageHelper.setSubject(mailBody.getSubject());
            javaMailSender.send(mimeMessage);
        }catch (MessagingException exception){
            System.out.println("Can't not send email");

        }
    }
}
