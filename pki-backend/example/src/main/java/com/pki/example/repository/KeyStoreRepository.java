package com.pki.example.repository;

import com.pki.example.model.entity.KeyStoreMeta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeyStoreRepository extends JpaRepository<KeyStoreMeta, Integer> {
}
