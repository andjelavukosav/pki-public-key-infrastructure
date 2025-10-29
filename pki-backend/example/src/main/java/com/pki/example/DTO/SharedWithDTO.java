package com.pki.example.DTO;

import lombok.Data;

@Data
public class SharedWithDTO {
    private Long userId;
    private String encryptedPasswordForUser;

    public SharedWithDTO() {}

    public SharedWithDTO(Long userId, String encryptedPasswordForUser) {
        this.userId = userId;
        this.encryptedPasswordForUser = encryptedPasswordForUser;
    }
}
