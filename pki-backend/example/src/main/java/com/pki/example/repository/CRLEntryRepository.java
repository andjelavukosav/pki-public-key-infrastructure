package com.pki.example.repository;

import com.pki.example.model.entity.CRLEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CRLEntryRepository extends JpaRepository<CRLEntry, Long> {
    boolean existsBySerialNumber(String serialNumber);
    List<CRLEntry> findAll();
}
