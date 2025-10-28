package com.pki.example.service.impl;

import com.pki.example.DTO.*;
import com.pki.example.model.entity.Certificate;
import com.pki.example.model.entity.CertificateSigningRequest;
import com.pki.example.model.entity.User;
import com.pki.example.model.enums.CsrStatus;
import com.pki.example.repository.CertificateRepository;
import com.pki.example.repository.CsrRepository;
import com.pki.example.service.CertificateService;
import com.pki.example.service.CsrService;
import com.pki.example.service.UserService;
import com.pki.example.service.CsrParseResult;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import javax.transaction.Transactional;
import java.security.PublicKey;
import java.security.Security;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CsrServiceImpl implements CsrService {

    @Autowired
    private CsrRepository csrRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private UserService userService;

    static {
        // register BouncyCastle once when class loads
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    public CsrUploadResponseDTO uploadCSR(CsrUploadRequestDTO request) {

        // 1. Validacija fajla
        validateCSRFile(request.getCsrFile());

        // 2. Čitaj PEM sadržaj
        String pemContent = readFileContent(request.getCsrFile());

        // 3. Parse CSR i izvuci podatke
        CsrParseResult parseResult = parseCSRFromPEM(pemContent);

        // 4. Validuj odabrani CA
        Certificate selectedCA = certificateRepository.findById(request.getSelectedCaId())
                .orElseThrow(() -> new RuntimeException("Selected CA not found"));

        validateCACanIssue(selectedCA);

        // 5. Validuj trajanje
        validateRequestedDuration(request.getRequestedDurationDays(), selectedCA);

        // 6. Sačuvaj CSR entity
        CertificateSigningRequest csr = new CertificateSigningRequest();
        csr.setFilename(request.getCsrFile().getOriginalFilename());
        csr.setPemContent(pemContent);
        csr.setSubject(parseResult.getSubject());
        csr.setPublicKeyAlgorithm(parseResult.getPublicKeyAlgorithm());
        csr.setKeySize(parseResult.getKeySize());
        csr.setSelectedCaId(request.getSelectedCaId());
        csr.setRequestedDurationDays(request.getRequestedDurationDays());
        csr.setUploadedAt(LocalDateTime.now());
        csr.setUploadedByUserId(getCurrentUserId());
        csr.setStatus(CsrStatus.PENDING);
        csr.setPublicKey(parseResult.getPublicKeyBase64());

        CertificateSigningRequest saved = csrRepository.save(csr);

        Long id = saved.getId().longValue();

        return new CsrUploadResponseDTO(
                id,
                "CSR uploaded successfully. Awaiting admin approval."
        );
    }

    private CsrParseResult parseCSRFromPEM(String pemContent) {
        try {
            // Ukloni PEM headers
            String base64Content = pemContent
                    .replaceAll("-----BEGIN CERTIFICATE REQUEST-----", "")
                    .replaceAll("-----END CERTIFICATE REQUEST-----", "")
                    .replaceAll("\\s", "");

            // Decode iz Base64
            byte[] csrBytes = Base64.getDecoder().decode(base64Content);

            // Parse sa BouncyCastle
            PKCS10CertificationRequest pkcs10 = new PKCS10CertificationRequest(csrBytes);

            // Izvuci podatke
            String subject = pkcs10.getSubject().toString();
            SubjectPublicKeyInfo pubKeyInfo = pkcs10.getSubjectPublicKeyInfo();
            String algorithm = pubKeyInfo.getAlgorithm().getAlgorithm().getId();
            int keySize = calculateKeySize(pubKeyInfo);
            String publicKeyBase64 = Base64.getEncoder().encodeToString(pubKeyInfo.getEncoded());

            // Kreiraj plain objekt bez Lombok builder-a
            CsrParseResult result = new CsrParseResult();
            result.setSubject(subject);
            result.setPublicKeyAlgorithm(algorithm);
            result.setKeySize(keySize);
            result.setPkcs10(pkcs10);
            result.setPublicKeyBase64(publicKeyBase64);

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse CSR: " + e.getMessage());
        }
    }

    private void validateCSRFile(MultipartFile csrFile) {
        if (csrFile == null || csrFile.isEmpty()) {
            throw new RuntimeException("CSR file is missing or empty.");
        }

        String filename = csrFile.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".pem") && !filename.endsWith(".csr"))) {
            throw new RuntimeException("CSR file must be a .pem or .csr file.");
        }
    }

    private String readFileContent(MultipartFile file) {
        try {
            return new String(file.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Failed to read CSR file content: " + e.getMessage(), e);
        }
    }

    private void validateCACanIssue(Certificate caCert) {
        if (caCert == null) {
            throw new RuntimeException("CA certificate not provided.");
        }
        if (!caCert.isIntermediate()) {
            throw new RuntimeException("Selected certificate is not a valid CA.");
        }
        if (caCert.isRevoked()) {
            throw new RuntimeException("Selected CA is revoked and cannot issue certificates.");
        }
        if (caCert.getEndDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Selected CA has expired.");
        }
    }

    private void validateRequestedDuration(Integer requestedDays, Certificate caCert) {
        if (requestedDays == null || requestedDays <= 0) {
            throw new RuntimeException("Requested duration must be positive.");
        }
        long maxDays = java.time.Duration.between(
                LocalDateTime.now(),
                caCert.getEndDate().atStartOfDay()
        ).toDays();

        if (requestedDays > maxDays) {
            throw new RuntimeException("Requested duration exceeds CA certificate validity.");
        }
    }

    private int calculateKeySize(SubjectPublicKeyInfo pubKeyInfo) {
        try {
            // Pretvori SubjectPublicKeyInfo u standardni Java PublicKey
            PublicKey publicKey = new JcaPEMKeyConverter()
                    .setProvider("BC")
                    .getPublicKey(pubKeyInfo);

            if (publicKey instanceof RSAPublicKey) {
                // RSA -> keySize je dužina modula
                return ((RSAPublicKey) publicKey).getModulus().bitLength();
            } else if (publicKey instanceof ECPublicKey) {
                // EC -> keySize je dužina order-a krive
                return ((ECPublicKey) publicKey).getParams().getCurve().getField().getFieldSize();
            } else {
                throw new RuntimeException("Unsupported key algorithm: " + publicKey.getAlgorithm());
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to determine key size: " + e.getMessage(), e);
        }
    }

    private Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();

        if (principal instanceof Integer) {
            return (Integer) principal;
        }

        System.out.println("DEBUG: Cannot determine userId from principal: " + principal);
        return 1;
    }

    @Transactional()
    public List<CertificateSigningRequest> getPendingCSRsForCAOrganization(String organization) {
        List<Certificate> caCerts = certificateRepository.findAllByOrganization(organization);

        if (caCerts.isEmpty()) {
            return Collections.emptyList();
        }

        // Sada je Certificate.getId() već Integer, tako da ne treba konverzija
        List<Integer> caIds = caCerts.stream()
                .map(Certificate::getId)
                .collect(Collectors.toList());

        return csrRepository.findBySelectedCaIdInAndStatus(caIds, CsrStatus.PENDING);
    }

    @Transactional
    public void processCSRDecision(CsrDecisionDTO decision, String processedByEmail) {

        CertificateSigningRequest csr = csrRepository.findById(decision.getCsrId().intValue())
                .orElseThrow(() -> new RuntimeException("CSR not found"));

        if (!csr.getStatus().equals(CsrStatus.PENDING)) {
            throw new RuntimeException("CSR is not pending.");
        }

        User processedBy = userService.findByEmail(processedByEmail);

        if (decision.isApproved()) {
            CertificateRequestDTO certReq = new CertificateRequestDTO();
            certReq.setIssuerId(csr.getSelectedCaId());  // Sada je već Integer
            certReq.setCn(extractCNFromSubject(csr.getSubject()));
            certReq.setO(extractOFromSubject(csr.getSubject()));
            certReq.setOu(extractOUFromSubject(csr.getSubject()));
            certReq.setC(extractCFromSubject(csr.getSubject()));
            certReq.setDurationInDays(decision.getFinalDurationDays() != null ?
                    decision.getFinalDurationDays() : csr.getRequestedDurationDays());
            certReq.setPublicKey(csr.getPublicKey());
            certReq.setEndEntity(true);

            CertificateResponseDTO issuedCert = certificateService.issueCertificate(certReq);

            csr.setIssuedCertificateId(issuedCert.getId());
            csr.setStatus(CsrStatus.ISSUED);
        } else {
            csr.setStatus(CsrStatus.REJECTED);
            csr.setRejectionReason(decision.getRejectionReason());
        }
        csr.setProcessedByUserId(processedBy.getId().intValue());
        csr.setProcessedAt(LocalDateTime.now());
        csrRepository.save(csr);
    }


    private String extractField(String subject, String field) {
        if (subject == null) return null;
        for (String part : subject.split(",")) {
            part = part.trim();
            if (part.startsWith(field + "=")) {
                return part.substring((field + "=").length());
            }
        }
        return null;
    }

    private String extractCNFromSubject(String subject) { return extractField(subject, "CN"); }
    private String extractOFromSubject(String subject)  { return extractField(subject, "O"); }
    private String extractOUFromSubject(String subject) { return extractField(subject, "OU"); }
    private String extractCFromSubject(String subject)  { return extractField(subject, "C"); }

}