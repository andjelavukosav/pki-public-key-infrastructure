package com.pki.example.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/csr")
public class CsrController {

    private final CertificateService certificateService;

    public CsrController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @PostMapping("/submit")
    public ResponseEntity<?> handleCsrUpload(
            @RequestParam("file") MultipartFile csrFile,
            @RequestParam("ca") String caName,
            @RequestParam("duration") int durationDays) {
        try {
            byte[] signedCert = certificateService.processCsr(csrFile, caName, durationDays);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"certificate.pem\"")
                    .body(new String(signedCert));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Greška: " + e.getMessage());
        }
    }
}

