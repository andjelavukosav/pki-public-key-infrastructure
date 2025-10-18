package com.pki.example.DTO;
import com.pki.example.validation.ValidationConstants;

import javax.validation.constraints.*;
import java.util.Map;


//KADA KORISNIK POPUNI FORMU ZA IZDAVANJE SERTIFIKATA BACK PRIMA OVO

public class CertificateRequestDTO {

    @NotBlank(message = "Common Name (CN) is required")
    @Size(min = ValidationConstants.MIN_CN_LENGTH,
            max = ValidationConstants.MAX_CN_LENGTH,
            message = "CN must be between {min} and {max} characters")
    @Pattern(regexp = ValidationConstants.CN_PATTERN,
            message = ValidationConstants.CN_INVALID_MSG)
    public String cn;  // Podaci o vlasniku (X500Name string ili posebna polja)

    @NotBlank(message = "Organization (O) is required")
    @Size(min = ValidationConstants.MIN_O_LENGTH,
            max = ValidationConstants.MAX_O_LENGTH,
            message = "Organization must be between {min} and {max} characters")
    @Pattern(regexp = ValidationConstants.O_PATTERN,
            message = ValidationConstants.O_INVALID_MSG)
    public String o;

    @Size(min = ValidationConstants.MIN_OU_LENGTH,
            max = ValidationConstants.MAX_OU_LENGTH,
            message = "OU must be between {min} and {max} characters")
    @Pattern(regexp = ValidationConstants.OU_PATTERN,
            message = ValidationConstants.OU_INVALID_MSG)
    public String ou;

    @NotBlank(message = "Country (C) is required")
    @Size(min = ValidationConstants.COUNTRY_LENGTH,
            max = ValidationConstants.COUNTRY_LENGTH,
            message = "Country must be exactly {min} characters")
    @Pattern(regexp = ValidationConstants.COUNTRY_PATTERN,
            message = ValidationConstants.COUNTRY_INVALID_MSG)
    public String c;

    // issuerId može biti null za root sertifikate
    public Integer issuerId;  // ID CA sertifikata koji potpisuje

    @NotNull(message = "Duration is required")
    @Min(value = ValidationConstants.MIN_DURATION_DAYS,
            message = "Duration must be at least {value} day")
    @Max(value = ValidationConstants.MAX_ROOT_DURATION_DAYS,
            message = "Duration cannot exceed {value} days")
    public int durationInDays; // trajanje sertifikata

    public boolean isRoot;
    public boolean isIntermediate;
    public boolean isEndEntity;
    public boolean isCA;

    public Map<String, String> extensions;   // npr. keyUsage, basicConstraints, itd.
    private String publicKey;

    public CertificateRequestDTO() {}

    public CertificateRequestDTO(String cn, String o, String ou, String c, Integer issuerId,
                                 int durationInDays, boolean isRoot, boolean isIntermediate,
                                 boolean isEndEntity, boolean isCA, Map<String, String> extensions) {
        this.cn = cn;
        this.o = o;
        this.ou = ou;
        this.c = c;
        this.issuerId = issuerId;
        this.durationInDays = durationInDays;
        this.isRoot = isRoot;
        this.isIntermediate = isIntermediate;
        this.isEndEntity = isEndEntity;
        this.isCA = isCA;
        this.extensions = extensions;
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getO() {
        return o;
    }

    public void setO(String o) {
        this.o = o;
    }

    public String getOu() {
        return ou;
    }

    public void setOu(String ou) {
        this.ou = ou;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public Integer getIssuerId() {
        return issuerId;
    }

    public void setIssuerId(Integer issuerId) {
        this.issuerId = issuerId;
    }

    public int getDurationInDays() {
        return durationInDays;
    }

    public void setDurationInDays(int durationInDays) {
        this.durationInDays = durationInDays;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    public boolean isIntermediate() {
        return isIntermediate;
    }

    public void setIntermediate(boolean intermediate) {
        isIntermediate = intermediate;
    }

    public boolean isEndEntity() {
        return isEndEntity;
    }

    public void setEndEntity(boolean endEntity) {
        isEndEntity = endEntity;
    }

    public boolean isCA() {
        return isCA;
    }

    public void setCA(boolean CA) {
        isCA = CA;
    }

    public Map<String, String> getExtensions() {
        return extensions;
    }

    public void setExtensions(Map<String, String> extensions) {
        this.extensions = extensions;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}