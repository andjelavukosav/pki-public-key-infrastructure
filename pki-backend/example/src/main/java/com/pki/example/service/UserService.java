package com.pki.example.service;

import com.pki.example.DTO.UserRegistrationDTO;
import com.pki.example.model.entity.User;

public interface UserService {
    User findByEmail(String email);
    void registerUser(UserRegistrationDTO dto);
    void verify(String rawToken);

    String sendPasswordResetLink(String email);
    void sendResetPasswordEmailAfterCommit(String email, String token);
    void resetPassword(String rawToken, String newPassword);
}
