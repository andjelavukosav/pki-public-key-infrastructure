package com.pki.example.service;

import com.pki.example.DTO.CsrDecisionDTO;
import com.pki.example.DTO.CsrUploadRequestDTO;
import com.pki.example.DTO.CsrUploadResponseDTO;
import com.pki.example.model.entity.CertificateSigningRequest;

import java.util.List;

public interface CsrService {
    CsrUploadResponseDTO uploadCSR(CsrUploadRequestDTO request);
    List<CertificateSigningRequest> getPendingCSRsForCAOrganization(String organization);
    void processCSRDecision(CsrDecisionDTO decision, String processedByEmail);

}
