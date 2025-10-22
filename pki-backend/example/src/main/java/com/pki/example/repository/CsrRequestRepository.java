package com.pki.example.repository;

import com.pki.example.model.entity.CertificateSigningRequest;
import com.pki.example.model.entity.CsrRequest;
import com.pki.example.model.enums.CsrStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CsrRequestRepository extends JpaRepository<CsrRequest, Long> {
    List<CsrRequest> findByStatus(CsrRequest.Status status);

}

