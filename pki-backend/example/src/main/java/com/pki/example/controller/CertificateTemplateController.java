package com.pki.example.controller;

import com.pki.example.DTO.CertificateTemplateDTO;
import com.pki.example.model.entity.CertificateTemplate;
import com.pki.example.service.CertificateTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
public class CertificateTemplateController {

    @Autowired
    private CertificateTemplateService service;

    @PostMapping("/create")
    public ResponseEntity<CertificateTemplateDTO> createTemplate(
            @RequestParam Long userId,
            @RequestBody CertificateTemplateDTO dto) {

        CertificateTemplate saved = service.createTemplate(userId, dto);

        CertificateTemplateDTO out = new CertificateTemplateDTO();
        out.setId(saved.getId());
        out.setName(saved.getName());
        out.setTtlDays(saved.getTtlDays());
        out.setCommonNameRegex(saved.getCommonNameRegex());
        out.setSubjectAltNameRegex(saved.getSubjectAltNameRegex());
        out.setKeyUsage(saved.getKeyUsage());
        out.setExtendedKeyUsage(saved.getExtendedKeyUsage());
        out.setIssuerId(saved.getIssuer().getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(out);
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
