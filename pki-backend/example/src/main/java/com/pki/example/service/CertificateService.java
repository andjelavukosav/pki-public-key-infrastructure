package com.pki.example.service;

import com.pki.example.DTO.CertificateRequestDTO;
import com.pki.example.DTO.CertificateResponseDTO;

import java.util.List;

public interface CertificateService {
    CertificateResponseDTO issueCertificate(CertificateRequestDTO request);
    CertificateResponseDTO getCertificateById(int id);
    List<CertificateResponseDTO> getAllCertificates();

}
