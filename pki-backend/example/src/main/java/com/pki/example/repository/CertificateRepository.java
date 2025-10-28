package com.pki.example.repository;

import com.pki.example.model.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CertificateRepository extends JpaRepository<Certificate, Integer> {

    @Query("SELECT c FROM Certificate c WHERE c.o = :organization AND c.isCA = true AND c.isIntermediate = true")
    List<Certificate> findAllByOrganization(@Param("organization") String organization);

    @Query("SELECT c FROM Certificate c WHERE c.o = :organization")
    List<Certificate> findAllByOrganizationList(@Param("organization") String organization);

    @Query("""
        SELECT c FROM Certificate c
        WHERE c.id IN (
            SELECT csr.issuedCertificateId
            FROM CertificateSigningRequest csr
            WHERE csr.uploadedByUserId = :userId
        )
    """)
    List<Certificate> findEndEntityCertificatesByUserId(@Param("userId") Integer userId);
    List<Certificate> findByIsCATrue();
}
