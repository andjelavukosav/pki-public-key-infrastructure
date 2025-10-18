package com.pki.example.auth;


import com.pki.example.exception.BadRequestException;
import com.pki.example.model.enums.UserRole;
import com.pki.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.pki.example.auth.JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    private final RecaptchaService recaptchaService;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        // Provjera reCAPTCHA
        boolean captchaValid = recaptchaService.verify(request.getRecaptchaToken());
        if (!captchaValid) {
            throw new BadRequestException("reCAPTCHA verification failed");
        }

        // Autentifikacija korisnika
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        //ako smo ovdje onda je iza kulisa izvrsena autentifikacija i provjerene su sve metode iz UserDetails
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Generisanje tokena sa claimovima
        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .message("Login successful")
                .build();
    }
}