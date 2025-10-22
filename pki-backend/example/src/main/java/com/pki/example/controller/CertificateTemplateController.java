package com.pki.example.controller;

import com.pki.example.DTO.CertificateTemplateDTO;
import com.pki.example.model.entity.CertificateTemplate;
import com.pki.example.service.CertificateTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
public class CertificateTemplateController {

    @Autowired
    private CertificateTemplateService service;

    @PostMapping("/create")
    public ResponseEntity<CertificateTemplate> createTemplate(
            @RequestParam Integer userId,
            @RequestBody CertificateTemplateDTO dto) {
        return ResponseEntity.ok(service.createTemplate(userId, dto));
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<CertificateTemplate>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getTemplatesByUser(userId));
    }

    @GetMapping("/by-issuer/{issuerId}")
    public ResponseEntity<List<CertificateTemplateDTO>> getByCertificate(@PathVariable Integer issuerId) {
        return ResponseEntity.ok(service.getTemplatesByCertificate(issuerId));
    }
}
