package com.pki.example.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "shared_password_entry")
public class SharedPasswordEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long passwordEntryId;

    @Column(nullable = false)
    private Long sharedWithUserId;

    @Column(nullable = false)
    private LocalDateTime sharedAt;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String encryptedPassword;

    public SharedPasswordEntry() {
    }

    public SharedPasswordEntry(Long passwordEntryId, Long sharedWithUserId, String encryptedPassword) {
        this.passwordEntryId = passwordEntryId;
        this.sharedWithUserId = sharedWithUserId;
        this.encryptedPassword = encryptedPassword;
        this.sharedAt = LocalDateTime.now();
    }

}