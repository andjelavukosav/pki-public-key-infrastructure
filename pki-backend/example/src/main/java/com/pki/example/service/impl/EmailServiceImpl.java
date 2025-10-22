package com.pki.example.service.impl;

import com.pki.example.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    public final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async // da bi se izvrsilo u posebnom thread-u
    @Override
    public void sendResetPasswordEmail(String toEmail, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Password Reset Request");
        message.setText("To reset your password, please click the following link: " + resetLink);
        mailSender.send(message);
    }
}
