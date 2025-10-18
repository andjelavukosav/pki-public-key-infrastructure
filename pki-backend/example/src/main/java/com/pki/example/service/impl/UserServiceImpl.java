package com.pki.example.service.impl;

import com.pki.example.DTO.UserRegistrationDTO;
import com.pki.example.model.entity.User;
import com.pki.example.model.enums.UserRole;
import com.pki.example.repository.UserRepository;
import com.pki.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerUser(UserRegistrationDTO dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("Lozinke se ne poklapaju!");
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email već postoji!");
        }

        if (!isValidPassword(dto.getPassword())) {
            throw new RuntimeException("Lozinka ne ispunjava minimalne zahteve!");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setOrganization(dto.getOrganization());
        user.setRole(UserRole.USER);
        //DOK URADIMO VERIFIKACIJU PREKO MEJLA
        user.setEnabled(true);
        user.setBlocked(false);
        userRepository.save(user);
    }

    private boolean isValidPassword(String password) {
        // Primer minimalnih zahteva: minimum 8 karaktera, bar 1 broj i 1 veliko slovo
        return password.matches("^(?=.*[0-9])(?=.*[A-Z]).{8,}$");
    }
}
