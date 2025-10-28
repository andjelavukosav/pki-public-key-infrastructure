package com.pki.example.repository;

import com.pki.example.model.entity.CertificateSigningRequest;
import com.pki.example.model.enums.CsrStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CsrRepository extends JpaRepository<CertificateSigningRequest, Integer> {
    @Query("SELECT c FROM CertificateSigningRequest c WHERE c.selectedCaId IN :caIds AND c.status = :status")
    List<CertificateSigningRequest> findBySelectedCaIdInAndStatus(List<Integer> caIds, CsrStatus status);
}