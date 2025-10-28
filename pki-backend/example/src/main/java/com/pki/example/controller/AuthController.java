package com.pki.example.controller;

import com.pki.example.DTO.ResetPasswordRequest;
import com.pki.example.DTO.UserRegistrationDTO;
import com.pki.example.auth.*;
import com.pki.example.model.entity.User;
import com.pki.example.service.EmailService;
import com.pki.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final EmailService emailService;
    private final SessionRegistry sessionRegistry;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody @Valid UserRegistrationDTO dto) {
        try {
            userService.registerUser(dto);
            return ResponseEntity.ok("Registracija uspešna!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody AuthenticationRequest request,
            HttpServletRequest httpRequest) {
        AuthenticationResponse response = authenticationService.authenticate(request, httpRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam("token") String token) {
        try {
            userService.verify(token);
            return ResponseEntity.ok("Nalog je uspešno aktiviran.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> recoverAccount(@RequestBody Map<String, String>request){
        String email = request.get("email");
        String token = userService.sendPasswordResetLink(email);

        if(token != null){
            userService.sendResetPasswordEmailAfterCommit(email, token);
        }
        return ResponseEntity.ok(Map.of("message","If there is an account with that email, a recovery link has been sent."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request.getRawToken(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Password is successfully reset."));
    }

}
