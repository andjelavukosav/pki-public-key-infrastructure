package com.pki.example.repository;

import com.pki.example.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificateRepository extends JpaRepository<Certificate, Integer> {

}
