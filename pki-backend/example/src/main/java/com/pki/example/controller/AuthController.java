package com.pki.example.controller;

import com.pki.example.DTO.UserRegistrationDTO;
import com.pki.example.auth.AuthenticationRequest;
import com.pki.example.auth.AuthenticationResponse;
import com.pki.example.auth.AuthenticationService;
import com.pki.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

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
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request){
        AuthenticationResponse response = authenticationService.authenticate(request);
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

}
