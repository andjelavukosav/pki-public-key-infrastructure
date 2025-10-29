package com.pki.example.repository;

import com.pki.example.model.SharedPasswordEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SharedPasswordEntryRepositoryInterface extends JpaRepository<SharedPasswordEntry, Long> {
    List<SharedPasswordEntry> findBySharedWithUserId(Long userId);
    List<SharedPasswordEntry> findByPasswordEntryId(Long passwordEntryId);
}