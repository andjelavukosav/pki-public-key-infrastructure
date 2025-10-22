package com.pki.example.certificates;

import com.pki.example.data.Issuer;
import com.pki.example.data.Subject;
import com.pki.example.data.Certificate;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.*;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class CertificateExample {

    private static final Logger logger = LoggerFactory.getLogger(CertificateExample.class);

    public Subject generateSubject() {
        logger.info("Generisanje subjekta sertifikata započeto.");
        KeyPair keyPairSubject = generateKeyPair();

        //klasa X500NameBuilder pravi X500Name objekat koji predstavlja podatke o vlasniku
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, "Ivana Kovacevic");
        builder.addRDN(BCStyle.SURNAME, "Kovacevic");
        builder.addRDN(BCStyle.GIVENNAME, "Ivana");
        builder.addRDN(BCStyle.O, "UNS-FTN");
        builder.addRDN(BCStyle.OU, "Katedra za informatiku");
        builder.addRDN(BCStyle.C, "RS");
        builder.addRDN(BCStyle.E, "kovacevic.ivana@uns.ac.rs");
        //UID (USER ID) je ID korisnika
        builder.addRDN(BCStyle.UID, "123456");

        return new Subject(keyPairSubject.getPublic(), builder.build());
    }

    public Issuer generateIssuer() {
        KeyPair kp = generateKeyPair();
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, "IT sluzba");
        builder.addRDN(BCStyle.SURNAME, "sluzba");
        builder.addRDN(BCStyle.GIVENNAME, "IT");
        builder.addRDN(BCStyle.O, "UNS-FTN");
        builder.addRDN(BCStyle.OU, "Katedra za informatiku");
        builder.addRDN(BCStyle.C, "RS");
        builder.addRDN(BCStyle.E, "itsluzba@uns.ac.rs");
        //UID (USER ID) je ID korisnika
        builder.addRDN(BCStyle.UID, "654321");

        //Kreiraju se podaci za issuer-a, sto u ovom slucaju ukljucuje:
        // - privatni kljuc koji ce se koristiti da potpise sertifikat koji se izdaje
        // - podatke o vlasniku sertifikata koji izdaje nov sertifikat
        return new Issuer(kp.getPrivate(), kp.getPublic(), builder.build());
    }

    public KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(2048, random);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            logger.error("Greška pri generisanju para ključeva: {}", e.getMessage(), e);
        }
        return null;
    }

    public com.pki.example.data.Certificate getCertificate() {

        try {
            Issuer issuer = generateIssuer();
            Subject subject = generateSubject();

            //Datumi od kad do kad vazi sertifikat
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = sdf.parse("2025-07-02");
            Date endDate = sdf.parse("2030-07-02");

            X509Certificate certificate = CertificateGenerator.generateCertificate(subject,
                    issuer, startDate, endDate, "1");

            return new com.pki.example.data.Certificate(subject, issuer,
                    "1", startDate, endDate, certificate);
        } catch (ParseException e) {
            logger.error("Greška pri parsiranju datuma: {}", e.getMessage(), e);
        }

        return null;
    }


}
