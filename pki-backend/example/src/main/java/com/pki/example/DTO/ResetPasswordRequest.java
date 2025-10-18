package com.pki.example.DTO;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String rawToken;
    private String newPassword;
}
