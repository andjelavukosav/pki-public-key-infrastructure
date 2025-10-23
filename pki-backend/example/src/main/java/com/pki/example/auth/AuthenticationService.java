package com.pki.example.auth;


import com.pki.example.exception.BadRequestException;
import com.pki.example.model.enums.UserRole;
import com.pki.example.repository.UserRepository;
import com.pki.example.util.DeviceUtils;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.pki.example.auth.JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    private final RecaptchaService recaptchaService;
    private final SessionRegistry sessionRegistry;

    public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletRequest httpRequest) {

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

        // Generisanje jti (UUID) za sesiju
        String sessionId = java.util.UUID.randomUUID().toString();

        // Prikupljanje IP i User-Agent
        String ipAddress = getClientIpAddress(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        String device = DeviceUtils.getDeviceName(userAgent);
        // Kreiranje SessionInfo
        SessionInfo sessionInfo = new SessionInfo(
                sessionId,
                ipAddress,
                userAgent,
                new Date(),
                user.getId(),
                device
        );

        // Dodavanje u SessionRegistry
        sessionRegistry.addSession(sessionInfo);

        // Generisanje tokena sa claimovima
        var jwtToken = jwtService.generateToken(user, sessionId);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .sessionId(sessionId)
                .message("Login successful")
                .build();
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

}