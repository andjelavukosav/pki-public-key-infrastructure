package com.pki.example.service.impl;

import com.pki.example.DTO.UserRegistrationDTO;

import com.pki.example.model.entity.User;
import com.pki.example.model.entity.VerificationToken;
import com.pki.example.model.enums.UserRole;
import com.pki.example.repository.UserRepository;
import com.pki.example.repository.VerificationTokenRepository;
import com.pki.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final VerificationTokenRepository tokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    @Override
    @Transactional
    public void registerUser(UserRegistrationDTO dto) {
        if (userRepo.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email je već u upotrebi.");
        }

        User u = new User();
        u.setEmail(dto.getEmail());
        u.setPassword(passwordEncoder.encode(dto.getPassword()));
        u.setFirstName(dto.getFirstName());
        u.setLastName(dto.getLastName());
        u.setOrganization(dto.getOrganization());
        u.setRole(UserRole.USER);
        u.setEnabled(false);
        u.setBlocked(false);

        userRepo.save(u);

        // --- GENERISANJE TOKENA ---
        String rawToken = generateToken();
        String tokenHash = hashToken(rawToken);

        System.out.println("============================================");
        System.out.println("[REGISTER] RAW TOKEN  = " + rawToken);
        System.out.println("[REGISTER] HASH TOKEN = " + tokenHash);
        System.out.println("[REGISTER] HASH LENGTH = " + tokenHash.length());
        System.out.println("============================================");

        VerificationToken vt = new VerificationToken();
        vt.setTokenHash(tokenHash);
        vt.setUser(u);
        vt.setExpiresAt(Instant.now().plus(24, ChronoUnit.HOURS));
        vt.setUsed(false);
        tokenRepo.save(vt);

        // --- SLANJE EMAIL-A ---
        String activationLink = frontendBaseUrl + "/activate?token=" + rawToken;
        sendActivationMail(u.getEmail(), activationLink);

        System.out.println("[REGISTER] Activation link sent to: " + u.getEmail());
        System.out.println("[REGISTER] Activation link: " + activationLink);
        System.out.println("============================================");
    }

    @Override
    @Transactional
    public void verify(String rawToken) {
        System.out.println("============================================");
        System.out.println("[VERIFY] RAW TOKEN = " + rawToken);
        String tokenHash = hashToken(rawToken);
        System.out.println("[VERIFY] HASH TOKEN = " + tokenHash);
        System.out.println("[VERIFY] HASH LENGTH = " + tokenHash.length());
        System.out.println("============================================");

        VerificationToken vt = tokenRepo.findByTokenHash(tokenHash)
                .orElseThrow(() -> new RuntimeException("Nevalidan ili iskorišćen token."));

        if (vt.isUsed()) throw new RuntimeException("Token je već iskorišćen.");
        if (vt.getExpiresAt().isBefore(Instant.now()))
            throw new RuntimeException("Token je istekao.");

        User u = vt.getUser();
        u.setEnabled(true);
        vt.setUsed(true);

        System.out.println("[VERIFY] ✅ Token validan! Korisnik aktiviran: " + u.getEmail());
        System.out.println("============================================");
    }


    private String generateToken() {
        byte[] buf = new byte[32];
        new SecureRandom().nextBytes(buf);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buf);
    }

    private String hashToken(String raw) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(raw.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (Exception e) {
            throw new RuntimeException("Greška pri heširanju tokena");
        }
    }

    private void sendActivationMail(String to, String link) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom("anatamandjelavuk@gmail.com"); // isti kao spring.mail.username
        msg.setTo(to);
        msg.setSubject("Aktivacija naloga");
        msg.setText("Zdravo,\n\nKliknite na sledeći link da aktivirate nalog:\n" +
                link + "\n\nLink važi 24h i može se iskoristiti samo jednom.");
        mailSender.send(msg);
    }
}
