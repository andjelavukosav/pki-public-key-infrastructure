package com.pki.example.repository;

import com.pki.example.model.PasswordEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PasswordEntryRepositoryInterface extends JpaRepository<PasswordEntry, Long> {
    List<PasswordEntry> findByOwnerId(Long ownerId);
}
