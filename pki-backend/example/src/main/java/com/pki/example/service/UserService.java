package com.pki.example.service;

import com.pki.example.DTO.UserRegistrationDTO;

public interface UserService {
    void registerUser(UserRegistrationDTO dto);
    void verify(String rawToken);
}
