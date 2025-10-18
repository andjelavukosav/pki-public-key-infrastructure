package com.pki.example.service.impl;

import com.pki.example.DTO.CertificateRequestDTO;
import com.pki.example.DTO.CertificateResponseDTO;
import com.pki.example.config.CustomUserDetails;
import com.pki.example.model.Certificate;
import com.pki.example.repository.CertificateRepository;
import com.pki.example.service.CertificateGenerator;
import com.pki.example.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class CertificateServiceImpl implements CertificateService {
    private final CertificateRepository certificateRepository;

    @Autowired
    public CertificateServiceImpl(CertificateRepository certificateRepository) {
        this.certificateRepository = certificateRepository;
    }
    public CertificateResponseDTO issueCertificate(CertificateRequestDTO request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Object principal = auth.getPrincipal();
        Long userId;

        if (principal instanceof CustomUserDetails cud) {
            userId = cud.getUser().getId();
        } else if (principal instanceof com.pki.example.model.entity.User u) {
            userId = u.getId();
        } else if (principal instanceof Integer i) {
            userId = i.longValue();
        } else {
            throw new RuntimeException("Unexpected principal type: " + principal.getClass());
        }

        String role = auth.getAuthorities().isEmpty()
                ? null
                : auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

        if (request.issuerId == null) {
            if (!"ADMIN".equals(role)) {
                throw new RuntimeException("Only admin can create root certificate");
            }

            // 2) Generiši subject key pair (RSA 2048)
            KeyPair subjectKeyPair = generateRsaKeyPair();

            // 3) Serijski broj (hex) – koristimo isti i u X.509 i u bazi
            String serialHex = Long.toHexString(System.nanoTime());

            String alias = "cert-" + serialHex;
            // 4) Datumi
            LocalDate start = LocalDate.now();
            LocalDate end = start.plusDays(request.durationInDays);

            // 5) Subject/Issuer DN (pretpostavka: request.subject i issuer.getSubject() su X500 DN stringovi, npr "CN=John, O=Org, C=RS")
            String subjectDn = "CN=" + request.cn +
                    ", O=" + request.o +
                    (request.ou != null && !request.ou.isBlank() ? ", OU=" + request.ou : "") +
                    ", C=" + request.c;

            // 6) Issuer PrivateKey (decode iz Base64 PKCS#8)
            PrivateKey issuerPrivateKey = subjectKeyPair.getPrivate();
            String issuerDn = subjectDn;


            // 7) Generiši X.509
            X509Certificate x509;
            try {
                x509 = CertificateGenerator.generateCertificate(
                        subjectDn,
                        issuerDn,
                        serialHex,
                        start,
                        end,
                        subjectKeyPair,
                        issuerPrivateKey,
                        null,
                        request.isRoot,
                        request.isIntermediate,
                        request.isEndEntity,
                        request.isCA,
                        request.extensions
                );
            } catch (Exception e) {
                throw new RuntimeException("Failed to generate certificate: " + e.getMessage(), e);
            }

            Map<String, String> values = CertificateGenerator.parseDN(subjectDn);

            // 8) Upis u entitet
            Certificate certificate = new Certificate();
            certificate.setAlias(alias);
            certificate.setSerialNumber(serialHex);
            certificate.setCn(values.get("CN"));
            certificate.setO(values.get("O"));
            certificate.setOu(values.get("OU"));
            certificate.setC(values.get("C"));
            certificate.setIssuer(issuerDn);
            certificate.setStartDate(start);
            certificate.setEndDate(end);
            certificate.setRoot(request.isRoot);
            certificate.setIntermediate(request.isIntermediate);
            certificate.setEndEntity(request.isEndEntity);
            certificate.setCA(request.isCA);
            certificate.setRevoked(false);
            certificate.setExtensions(String.valueOf(request.extensions)); // možeš JSON stringify ako želiš

        /*    // 1. Cuvanje u keystore
            KeyStoreMeta meta = keyStoreService.createAndStoreKeyStore(
                    new X509Certificate[]{x509},
                    subjectKeyPair.getPrivate(),
                    alias,
                    userId
            );

// 2. Certificate entitet čuva samo referencu
            certificate.setKeyStoreMetaId(meta.getId());
*/
            Certificate saved = certificateRepository.save(certificate);

            return new CertificateResponseDTO(
                    saved.getId(),
                    saved.getAlias(),
                    saved.getSerialNumber(),
                    saved.getCn(),
                    saved.getO(),
                    saved.getOu(),
                    saved.getC(),
                    saved.getIssuer(),
                    saved.getStartDate(),
                    saved.getEndDate(),
                    saved.getIssuerId(),
                    saved.isRoot(),
                    saved.isIntermediate(),
                    saved.isEndEntity(),
                    saved.isCA(),
                    saved.isRevoked()
            );
        }


      /*  /// KREIRANJE SERTIFIKATA AKO ISSUER POSTOJI STAVLJANJE U LANAC ISPOD ROOT SERTIFIKATA
        Certificate issuer = certificateRepository.findById(request.issuerId).orElseThrow(() -> new RuntimeException("Issuer not found"));

        if (issuer.isRevoked()) {
            throw new RuntimeException("Issuer certificate is revoked.");
        }
        if (issuer.getEndDate() != null && issuer.getEndDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Issuer certificate is expired.");
        }
        if (!issuer.isCA()) {
            throw new RuntimeException("Issuer is not a CA; cannot issue new certificates.");
        }

        if (request.isIntermediate) {
            if (!issuer.isRoot()) {
                throw new RuntimeException("Intermediate certificate must be issued only by root cert");
            }

            if (!request.isCA) {
                throw new RuntimeException("Intermediate certificate must have isCA=true to issue other certificates.");
            }

            if (request.isRoot) {
                throw new RuntimeException("Certificate cannot be both root and intermediate.");
            }
        }

        if (request.isEndEntity) {
            if (!issuer.isIntermediate()) {
                throw new RuntimeException("End-entity certificate can only be issued by an intermediate certificate. " +
                        "Issuer (ID: " + issuer.getId() + ") is not an intermediate certificate.");
            }
            if (request.isCA) {
                throw new RuntimeException("End-entity certificate cannot be a CA.");
            }
            if (request.isRoot || request.isIntermediate) {
                throw new RuntimeException("End-entity certificate cannot be root or intermediate.");
            }
        }

        LocalDate requestedEnd = LocalDate.now().plusDays(request.durationInDays);
        if (requestedEnd.isAfter(issuer.getEndDate())) {
            throw new RuntimeException("Certificate validity period cannot exceed issuer's validity. " +
                    "Issuer expires on: " + issuer.getEndDate() +
                    ", requested end date: " + requestedEnd);
        }

        // 2) Generiši subject key pair (RSA 2048)
        KeyPair subjectKeyPair = generateRsaKeyPair();

        // 3) Serijski broj (hex) – koristimo isti i u X.509 i u bazi
        String serialHex = Long.toHexString(System.nanoTime());

        String alias = "cert-" + serialHex;

        // 4) Datumi
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(request.durationInDays);

        // 5) Subject/Issuer DN (pretpostavka: request.subject i issuer.getSubject() su X500 DN stringovi, npr "CN=John, O=Org, C=RS")
        String subjectDn = "CN=" + request.cn+
                ", O=" + request.o +
                (request.ou != null ? ", OU=" + request.ou : "") +
                ", C=" + request.c;
        String issuerDn = "CN=" + issuer.getCn() +
                ", O=" + issuer.getO() +
                (issuer.getOu() != null ? ", OU=" + issuer.getOu() : "") +
                ", C=" + issuer.getC();


        // 6) Issuer PrivateKey (decode iz Base64 PKCS#8)
        KeyStoreMeta issuerMeta = keyStoreService.getMetaById(issuer.getKeyStoreMetaId());
        PrivateKey issuerPrivateKey = keyStoreService.loadPrivateKey(
                issuerMeta,
                issuer.getAlias()
        );

        if (issuerPrivateKey == null) {
            throw new RuntimeException("Issuer private key is null for alias: " + issuer.getAlias());
        }

        X509Certificate issuerCert = keyStoreService.loadCertificate(issuerMeta, issuer.getAlias());

        if (issuerCert == null) {
            throw new RuntimeException("Issuer certificate is null for alias: " + issuer.getAlias());
        }

        X509Certificate x509;
        try {
            x509 = CertificateGenerator.generateCertificate(
                    subjectDn,
                    issuerDn,
                    serialHex,
                    start,
                    end,
                    subjectKeyPair,
                    issuerPrivateKey,
                    issuerCert,
                    request.isRoot,
                    request.isIntermediate,
                    request.isEndEntity,
                    request.isCA,
                    request.extensions
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate certificate: " + e.getMessage(), e);
        }

//        // 8) Upis u entitet
//        Certificate certificate = new Certificate();
//        certificate.setAlias(alias);
//        certificate.setSerialNumber(serialHex);
//        certificate.setCn(request.cn);
//        certificate.setO(request.o);
//        certificate.setOu(request.ou);
//        certificate.setC(request.c);
//        certificate.setIssuer(issuerDn);
//        certificate.setStartDate(start);
//        certificate.setEndDate(end);
//        certificate.setRoot(request.isRoot);
//        certificate.setIntermediate(request.isIntermediate);
//        certificate.setEndEntity(request.isEndEntity);
//        certificate.setCA(request.isCA);
//        certificate.setRevoked(false);
//        certificate.setExtensions(String.valueOf(request.extensions)); // možeš JSON stringify ako želiš

//        // 1. Cuvanje u keystore (subject cert i private key)
//        KeyStoreMeta meta = keyStoreService.createAndStoreKeyStore(
//                new X509Certificate[]{x509},
//                subjectKeyPair.getPrivate(),
//                alias,
//                userId
//        );

// KORISTI (novi kod sa chain-om):
        Certificate certificate = new Certificate();
        certificate.setAlias(alias);
        certificate.setSerialNumber(serialHex);
        certificate.setCn(request.cn);
        certificate.setO(request.o);
        certificate.setOu(request.ou);
        certificate.setC(request.c);
        certificate.setIssuer(issuerDn);
        certificate.setStartDate(start);
        certificate.setEndDate(end);
        certificate.setRoot(request.isRoot);
        certificate.setIntermediate(request.isIntermediate);
        certificate.setEndEntity(request.isEndEntity);
        certificate.setCA(request.isCA);
        certificate.setRevoked(false);
        certificate.setExtensions(String.valueOf(request.extensions));
        certificate.setIssuerId(issuer.getId()); // DODATO: čuvaj issuer ID!

        // Gradi kompletan chain
        List<X509Certificate> chainList = buildCertificateChain(certificate, x509);

        // Sačuvaj sa chain-om
        KeyStoreMeta meta = keyStoreService.createAndStoreKeyStore(
                chainList.toArray(new X509Certificate[0]),
                subjectKeyPair.getPrivate(),
                alias,
                userId
        );

// 2. Certificate entitet čuva samo referencu
        certificate.setKeyStoreMetaId(meta.getId());

        Certificate saved = certificateRepository.save(certificate);

        return new CertificateResponseDTO(
                saved.getId(),
                saved.getAlias(),
                saved.getSerialNumber(),
                saved.getCn(),
                saved.getO(),
                saved.getOu(),
                saved.getC(),
                saved.getIssuer(),
                saved.getStartDate(),
                saved.getEndDate(),
                saved.getIssuerId(),
                saved.isRoot(),
                saved.isIntermediate(),
                saved.isEndEntity(),
                saved.isCA(),
                saved.isRevoked()
        );*/
        return null;
    }

    private static KeyPair generateRsaKeyPair() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            return kpg.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("RSA not supported", e);
        }
    }

}
