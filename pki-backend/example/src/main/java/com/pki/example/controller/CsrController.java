package com.pki.example.controller;

import com.pki.example.DTO.CsrDecisionDTO;
import com.pki.example.DTO.CsrUploadRequestDTO;
import com.pki.example.DTO.CsrUploadResponseDTO;
import com.pki.example.model.entity.CertificateSigningRequest;
import com.pki.example.model.entity.User;
import com.pki.example.service.CertificateService;
import com.pki.example.service.CsrService;
import com.pki.example.service.UserService;
import com.pki.example.service.impl.CertificateServiceImpl;
import com.pki.example.validation.ValidationConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/csr")
@Validated
public class CsrController {

    @Autowired
    private CsrService csrService;

    @Autowired
    private UserService userService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadCSR(
            @RequestParam("csrFile")
            @NotNull(message = "CSR file is required")
            MultipartFile csrFile,

            @RequestParam("privateKeyFile")
            @NotNull(message = "Private key file is required")
            MultipartFile privateKeyFile,

            @RequestParam("selectedCaId")
            @NotNull(message = "CA selection is required")
            @Positive(message = "Selected CA ID must be positive")
            Integer selectedCaId,  // PROMENIO SA Long NA Integer

            @RequestParam("requestedDurationDays")
            @NotNull(message = "Duration is required")
            @Min(value = ValidationConstants.MIN_DURATION_DAYS,
                    message = "Duration must be at least {value} day")
            @Max(value = ValidationConstants.MAX_DURATION_DAYS,
                    message = "Duration cannot exceed {value} days")
            Integer duration) {

        // Manual validation for file extension
        if (csrFile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("CSR file is required");
        }

        String filename = csrFile.getOriginalFilename();
        if (filename == null || !isValidFileExtension(filename, ".csr", ".pem")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("CSR file must have .csr or .pem extension");
        }

        if (csrFile.getSize() > ValidationConstants.MAX_FILE_SIZE) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ValidationConstants.FILE_SIZE_EXCEEDED_MSG);
        }

        CsrUploadRequestDTO request = new CsrUploadRequestDTO();
        request.setCsrFile(csrFile);
        request.setSelectedCaId(selectedCaId);
        request.setRequestedDurationDays(duration);

        System.out.println("=== CSR Upload Request ===");
        System.out.println("Selected CA ID: " + selectedCaId);
        System.out.println("Requested Duration (days): " + duration);
        System.out.println("CSR file name: " + csrFile.getOriginalFilename());
        System.out.println("CSR file size: " + csrFile.getSize());
        System.out.println("Private key file name: " + privateKeyFile.getOriginalFilename());
        System.out.println("Private key file size: " + privateKeyFile.getSize());
        System.out.println("==========================");

        CsrUploadResponseDTO response = csrService.uploadCSR(request);
        return ResponseEntity.ok(response);
    }

    private boolean isValidFileExtension(String filename, String... allowedExtensions) {
        String lowerFilename = filename.toLowerCase();
        return Arrays.stream(allowedExtensions)
                .anyMatch(lowerFilename::endsWith);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('CA')")
    public ResponseEntity<List<CertificateSigningRequest>> getPendingCSRs(@RequestParam("userId") Long userId) {
        // Fetch the user's organization using userId
        User user = userService.findById(userId);
        String organization = user.getOrganization();

        List<CertificateSigningRequest> pending = csrService.getPendingCSRsForCAOrganization(organization);
        return ResponseEntity.ok(pending);
    }

    @PostMapping("/process")
    @PreAuthorize("hasRole('CA')")
    public ResponseEntity<?> processCSRDecision(
            @Valid @RequestBody CsrDecisionDTO decision,
            @RequestParam("userId") Long userId,  // PROMENIO SA Long NA Integer
            HttpServletRequest request) {

        String ipAddress = getClientIpAddress(request);
        String role = getCurrentUserRole();

        try {
            User caUser = userService.findById(userId);
            if (caUser == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("CA user not found for provided userId");
            }

            csrService.processCSRDecision(decision, caUser.getEmail());

            return ResponseEntity.ok(
                    Map.of(
                            "status", "SUCCESS",
                            "csrId", decision.getCsrId(),
                            "approved", decision.isApproved()
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            Map.of(
                                    "status", "FAILURE",
                                    "csrId", decision.getCsrId(),
                                    "message", e.getMessage()
                            )
                    );
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