package com.pki.example.service;

import com.pki.example.DTO.CertificateRequestDTO;
import com.pki.example.DTO.CertificateResponseDTO;
import com.pki.example.model.entity.Certificate;

import java.util.List;

public interface CertificateService {
    CertificateResponseDTO issueCertificate(CertificateRequestDTO request);
    CertificateResponseDTO getCertificateById(int id);
    List<CertificateResponseDTO> getAllCertificates();
    List<CertificateResponseDTO> getAllCACertificates();
    List<CertificateResponseDTO> getAllCACertificatesByOrg(String organization);
    void revokeCertificate(int id, String reason);
    List<Certificate> getEndEntityCertificatesForUser(Integer userId);
}
