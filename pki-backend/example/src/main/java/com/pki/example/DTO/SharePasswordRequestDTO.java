package com.pki.example.DTO;

public class SharePasswordRequestDTO {
    public Long passwordEntryId;
    public Long shareWithUserId;
    public String encryptedPasswordForUser; // enkriptovana javnim ključem drugog korisnika

    public SharePasswordRequestDTO() {}

    public SharePasswordRequestDTO(Long passwordEntryId, Long shareWithUserId, String encryptedPasswordForUser) {
        this.passwordEntryId = passwordEntryId;
        this.shareWithUserId = shareWithUserId;
        this.encryptedPasswordForUser = encryptedPasswordForUser;
    }
}