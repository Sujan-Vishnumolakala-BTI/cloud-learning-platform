package com.learningplatform.user_service.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(
            JavaMailSender mailSender) {

        this.mailSender = mailSender;
    }

    public void sendOtp(
            String email,
            String otp) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(email);

        message.setSubject(
                "CloudPath Email Verification");

        message.setText(
                "Your CloudPath verification code is: "
                + otp
                + "\n\n"
                + "This code expires in 10 minutes."
                + "\n\n"
                + "If you did not create this account, "
                + "you can ignore this email."
        );

        mailSender.send(message);
    }
}