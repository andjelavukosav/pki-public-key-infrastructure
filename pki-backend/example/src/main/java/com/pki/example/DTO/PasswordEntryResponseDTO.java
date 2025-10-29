package com.pki.example.DTO;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//@Data
/*public class PasswordEntryResponseDTO {
    private Long id;
    private String siteName;
    private String username;
    private String encryptedPassword;
    private LocalDateTime createdAt;

    public PasswordEntryResponseDTO() {
    }

    public PasswordEntryResponseDTO(Long id, String siteName, String username, String encryptedPassword, LocalDateTime createdAt) {
        this.id = id;
        this.siteName = siteName;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.createdAt = createdAt;
    }
}*/

@Data
public class PasswordEntryResponseDTO {
    private Long id;
    private String siteName;
    private String username;
    private String encryptedPassword;
    private Long ownerId; // NOVO
    private List<SharedWithDTO> sharedWith; // NOVO
    private LocalDateTime createdAt;

    public PasswordEntryResponseDTO() {}

    public PasswordEntryResponseDTO(Long id, String siteName, String username, String encryptedPassword,
                                    Long ownerId, LocalDateTime createdAt) {
        this.id = id;
        this.siteName = siteName;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
        this.ownerId = ownerId;
        this.sharedWith = sharedWith = new ArrayList<>();
        this.createdAt = createdAt;
    }
}
