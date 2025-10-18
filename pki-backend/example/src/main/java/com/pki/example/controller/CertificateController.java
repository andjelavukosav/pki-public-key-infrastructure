package com.pki.example.controller;

import com.pki.example.DTO.CertificateRequestDTO;
import com.pki.example.DTO.CertificateResponseDTO;
import com.pki.example.service.CertificateService;
import com.pki.example.service.CustomLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/certificates")
@Validated
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private CustomLoggerService loggerService;

    @PostMapping(value="/issue")
    public CertificateResponseDTO issueCertificate(
            @Valid @RequestBody CertificateRequestDTO request,
            HttpServletRequest httpRequest){
        String ipAddress = getClientIpAddress(httpRequest);
        String user = getCurrentUser();
        String role = getCurrentUserRole();

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
}
