package com.pki.example.service;

import com.pki.example.DTO.UserRegistrationDTO;
import com.pki.example.model.entity.User;

public interface UserService {
    void registerUser(UserRegistrationDTO dto);
    void verify(String rawToken);
    User findByEmail(String email);
    User findById(Integer id);
}
