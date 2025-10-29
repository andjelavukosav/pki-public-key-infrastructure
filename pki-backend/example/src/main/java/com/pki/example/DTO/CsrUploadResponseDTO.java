package com.pki.example.DTO;

public class CsrUploadResponseDTO {
    private Long id;  // PROMENI SA Integer NA Long
    private String status;

    public CsrUploadResponseDTO() {}

    public CsrUploadResponseDTO(Long id, String status) {  // PROMENI SA Integer NA Long
        this.id = id;
        this.status = status;
    }

    public Long getId() {  // PROMENI SA Integer NA Long
        return id;
    }

    public void setId(Long id) {  // PROMENI SA Integer NA Long
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}