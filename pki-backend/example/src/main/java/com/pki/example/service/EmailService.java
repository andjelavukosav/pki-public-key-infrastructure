package com.pki.example.service;

public interface EmailService {
    void sendResetPasswordEmail(String toEmail, String resetLink);
}
