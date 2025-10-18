package com.pki.example.service;

import com.pki.example.DTO.CertificateRequestDTO;
import com.pki.example.DTO.CertificateResponseDTO;

public interface CertificateService {
    CertificateResponseDTO issueCertificate(CertificateRequestDTO request);
}
