package com.pki.example.DTO;


public class CsrUploadResponseDTO {
    private Long id;
    private String status;

    public CsrUploadResponseDTO() {}

    public CsrUploadResponseDTO(Long id, String status) {
        this.id = id;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
