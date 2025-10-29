package com.pki.example.model.entity;

import com.pki.example.model.enums.CsrStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class CertificateSigningRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Metadata
    private String filename;        // originalnog .pem fajla
    private LocalDateTime uploadedAt;
    private Integer uploadedByUserId; // ko je upload-ovao

    // CSR sadržaj
    @Lob
    private String pemContent;      // Raw PEM format CSR
    private String subject;         // X500Name iz CSR-a
    private String publicKeyAlgorithm;
    private Integer keySize;

    // Request parametri
    @Column(name = "selected_ca_id")
    private Integer selectedCaId;   // koji CA je odabran
    private Integer requestedDurationDays;


    @Lob
    @Column(columnDefinition = "TEXT")
    private String publicKey; // Base64 enkodovan javni ključ

    // Status
    @Enumerated(EnumType.STRING)
    private CsrStatus status;
    private String rejectionReason;

    // Rezultat
    private Integer issuedCertificateId; // ID izdatog sertifikata
    private LocalDateTime processedAt;
    private Integer processedByUserId; // ko je odobrio/odbio

    public CertificateSigningRequest(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public Integer getUploadedByUserId() {
        return uploadedByUserId;
    }

    public void setUploadedByUserId(Integer uploadedByUserId) {
        this.uploadedByUserId = uploadedByUserId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPemContent() {
        return pemContent;
    }

    public void setPemContent(String pemContent) {
        this.pemContent = pemContent;
    }

    public String getPublicKeyAlgorithm() {
        return publicKeyAlgorithm;
    }

    public void setPublicKeyAlgorithm(String publicKeyAlgorithm) {
        this.publicKeyAlgorithm = publicKeyAlgorithm;
    }

    public Integer getKeySize() {
        return keySize;
    }

    public void setKeySize(Integer keySize) {
        this.keySize = keySize;
    }

    public Integer getSelectedCaId() {
        return selectedCaId;
    }

    public void setSelectedCaId(Integer selectedCaId) {
        this.selectedCaId = selectedCaId;
    }

    public Integer getRequestedDurationDays() {
        return requestedDurationDays;
    }

    public void setRequestedDurationDays(Integer requestedDurationDays) {
        this.requestedDurationDays = requestedDurationDays;
    }

    public CsrStatus getStatus() {
        return status;
    }

    public void setStatus(CsrStatus status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Integer getIssuedCertificateId() {
        return issuedCertificateId;
    }

    public void setIssuedCertificateId(Integer issuedCertificateId) {
        this.issuedCertificateId = issuedCertificateId;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public Integer getProcessedByUserId() {
        return processedByUserId;
    }

    public void setProcessedByUserId(Integer processedByUserId) {
        this.processedByUserId = processedByUserId;
    }


    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }
}
