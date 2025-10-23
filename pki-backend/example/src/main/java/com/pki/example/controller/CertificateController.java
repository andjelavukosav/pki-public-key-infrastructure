package com.pki.example.controller;

import com.pki.example.DTO.CertificateRequestDTO;
import com.pki.example.DTO.CertificateResponseDTO;
import com.pki.example.model.entity.User;
import com.pki.example.service.CertificateService;
import com.pki.example.service.CustomLoggerService;

import com.pki.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/certificates")
@Validated
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private UserService userService;

    @Autowired
    private CustomLoggerService loggerService;

    @PostMapping(value="/issue")
    public CertificateResponseDTO issueCertificate(
            @Valid @RequestBody CertificateRequestDTO request,
            HttpServletRequest httpRequest){
        String ipAddress = getClientIpAddress(httpRequest);
        String user = getCurrentUser();
        String role = getCurrentUserRole();

        User currentUser = userService.findByEmail(user);

        // Ako je CA korisnik, dozvoli samo za sopstvenu organizaciju i svoj lanac
        if ("ROLE_CA_USER".equals(role)) {
            String caOrg = currentUser.getOrganization();

            // 1️⃣ Provera da li target sertifikat pripada organizaciji
            if (request.getO() != null && !request.getO().equalsIgnoreCase(caOrg)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "CA can only issue certificates for their own organization");
            }

            // 2️⃣ Provera da li issuer sertifikat pripada lancu prijavljenog CA korisnika
            if (request.getIssuerId() != null) {
                CertificateResponseDTO issuerCert = certificateService.getCertificateById(request.getIssuerId());

                if (!issuerCert.getO().equalsIgnoreCase(caOrg) || !issuerCert.isCA()) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                            "CA user cannot use this issuer certificate (not in their chain or not CA)");
                }
            }
        }
        try {
            CertificateResponseDTO response = certificateService.issueCertificate(request);

            String issuerInfo = request.issuerId != null ? "Issuer ID: " + request.issuerId : "SELF_SIGNED";

            loggerService.logCertificateEvent(
                    "CERTIFICATE_ISSUED",
                    user,
                    role,
                    "SUCCESS",
                    "Certificate issued successfully",
                    ipAddress,
                    String.valueOf(response.getId()),
                    request.cn,
                    issuerInfo
            );

            return response;
        } catch (Exception e) {
            String issuerInfo = request.issuerId != null ? "Issuer ID: " + request.issuerId : "SELF_SIGNED";

            loggerService.logCertificateEvent(
                    "CERTIFICATE_ISSUE_FAILED",
                    user,
                    role,
                    "FAILURE",
                    "Failed to issue certificate: " + e.getMessage(),
                    ipAddress,
                    "N/A",
                    request.cn,
                    issuerInfo
            );
            throw e;
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    private String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }
        return "ANONYMOUS";
    }

    private String getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities() != null && !auth.getAuthorities().isEmpty()) {
            return auth.getAuthorities().iterator().next().getAuthority();
        }
        return "UNKNOWN";
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<CertificateResponseDTO> getById(@PathVariable int id, HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);
        String user = getCurrentUser();
        String role = getCurrentUserRole();

        try {
            CertificateResponseDTO cert = certificateService.getCertificateById(id);

            String issuerInfo = cert.getIssuer() != null ? cert.getIssuer() : "SELF_SIGNED";

            loggerService.logCertificateEvent(
                    "CERTIFICATE_ACCESSED",
                    user,
                    role,
                    "SUCCESS",
                    "Retrieved certificate details",
                    ipAddress,
                    String.valueOf(id),
                    cert.getCn(),
                    issuerInfo
            );

            return ResponseEntity.ok(cert);
        } catch (Exception e) {
            loggerService.logCertificateEvent(
                    "CERTIFICATE_ACCESS_FAILED",
                    user,
                    role,
                    "FAILURE",
                    "Failed to retrieve certificate: " + e.getMessage(),
                    ipAddress,
                    String.valueOf(id),
                    "UNKNOWN",
                    "UNKNOWN"
            );
            throw e;
        }
    }

    @GetMapping(value="/all")
    public ResponseEntity<List<CertificateResponseDTO>> getAll(HttpServletRequest request) {
        return ResponseEntity.ok(certificateService.getAllCertificates());
    }

    @GetMapping("/caOrg")
    public ResponseEntity<List<CertificateResponseDTO>> getAllCACertificatesByOrg(
            @RequestParam("userId") Long userId,
            HttpServletRequest request) {

        String ipAddress = getClientIpAddress(request);
        String username = getCurrentUser();
        String role = getCurrentUserRole();

        // Fetch the user's organization using userId
        User user = userService.findById(userId);
        String organization = user.getOrganization();

        loggerService.logCertificateEvent(
                "CA_CERTIFICATE_LIST_ACCESSED",
                username,
                role,
                "SUCCESS",
                "Retrieved all CA certificates for organization: " + organization,
                ipAddress,
                "N/A",
                "N/A",
                "N/A"
        );

        // Fetch certificates by organization and isCA=true
        return ResponseEntity.ok(
                certificateService.getAllCACertificatesByOrg(organization)
        );
    }

}
