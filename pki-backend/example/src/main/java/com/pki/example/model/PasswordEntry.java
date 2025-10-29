package com.pki.example.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "password_entry")
public class PasswordEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long ownerId;

    @Column(nullable = false)
    private String siteName;

    @Column(nullable = false)
    private String username;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String encryptedPassword; // Base64 enkriptovana lozinka

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public PasswordEntry() {
    }

    public PasswordEntry(Long ownerId, String siteName, String username, String encryptedPassword) {
        this.ownerId = ownerId;
        this.siteName = siteName;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.createdAt = LocalDateTime.now();
    }

}
