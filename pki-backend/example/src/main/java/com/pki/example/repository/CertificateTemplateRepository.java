package com.pki.example.repository;

import com.pki.example.model.entity.CertificateTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificateTemplateRepository extends JpaRepository<CertificateTemplate, Long> {
    List<CertificateTemplate> findByOwnerId(Long ownerId);

    List<CertificateTemplate> findByIssuerId(Integer issuerId);
}
