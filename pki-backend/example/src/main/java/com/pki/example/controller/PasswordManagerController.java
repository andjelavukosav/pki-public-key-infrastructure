package com.pki.example.controller;

import com.pki.example.DTO.PasswordEntryRequestDTO;
import com.pki.example.DTO.PasswordEntryResponseDTO;
import com.pki.example.DTO.SharePasswordRequestDTO;
import com.pki.example.model.entity.User;
import com.pki.example.service.PasswordManagerService;
import com.pki.example.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/password-manager")
public class PasswordManagerController {

    private final PasswordManagerService passwordManagerService;
    private final UserService userService;

    @PostMapping("/entries")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PasswordEntryResponseDTO> savePasswordEntry(@RequestBody PasswordEntryRequestDTO request, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        PasswordEntryResponseDTO response = passwordManagerService.savePasswordEntry(request, user.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/entries")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<PasswordEntryResponseDTO>> getUserPasswordEntries(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        List<PasswordEntryResponseDTO> entries = passwordManagerService.getUserPasswordEntries(user.getId());
        return ResponseEntity.ok(entries);
    }

    @PostMapping("/share")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> sharePassword(@RequestBody SharePasswordRequestDTO request, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        passwordManagerService.sharePassword(request, user.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/entries/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deletePasswordEntry(@PathVariable Long id) {
        passwordManagerService.deletePasswordEntry(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/public-key")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, String>> getUserPublicKey(Principal principal) {
        User currentUser = userService.findByEmail(principal.getName());
        String publicKey = passwordManagerService.getUserPublicKey(currentUser.getId());
        return ResponseEntity.ok(Map.of("publicKey", publicKey));
    }

    @GetMapping("/public-key/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, String>> getUserPublicKey(@PathVariable Integer userId) {
        String publicKey = passwordManagerService.getUserPublicKey(userId);
        return ResponseEntity.ok(Map.of("publicKey", publicKey));
    }
    @GetMapping("/users-with-keys")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Map<String, Object>>> getUsersWithKeys(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        List<Map<String, Object>> users = passwordManagerService.getUsersWithPublicKeys(user.getId());
        return ResponseEntity.ok(users);
    }
}