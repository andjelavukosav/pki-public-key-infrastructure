package com.pki.example.service;

import com.pki.example.DTO.CertificateTemplateDTO;
import com.pki.example.model.entity.Certificate;
import com.pki.example.model.entity.CertificateTemplate;
import com.pki.example.model.entity.User;
import com.pki.example.repository.CertificateRepository;
import com.pki.example.repository.CertificateTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CertificateTemplateService {

    @Autowired
    private CertificateTemplateRepository repo;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private CertificateRepository certificateRepository;
    @Autowired
    private UserService userService;

    public CertificateTemplate createTemplate(Integer userId, CertificateTemplateDTO dto) {
        Certificate issuer = certificateRepository.findById(dto.getIssuerId())
                .orElseThrow(() -> new RuntimeException("Issuer not found"));
        User owner = userService.findById(userId);//.orElseThrow(() -> new RuntimeException("User not found"));

        CertificateTemplate t = new CertificateTemplate();
        t.setName(dto.getName());
        t.setIssuer(issuer);
        t.setCommonNameRegex(dto.getCommonNameRegex());
        t.setSubjectAltNameRegex(dto.getSubjectAltNameRegex());
        t.setTtlDays(dto.getTtlDays());
        t.setKeyUsage(dto.getKeyUsage());
        t.setExtendedKeyUsage(dto.getExtendedKeyUsage());
        t.setOwner(owner);

        return repo.save(t);
    }

    @Transactional
    public List<CertificateTemplate> getTemplatesByUser(Long userId) {
        return repo.findByOwnerId(userId);
    }

    @Transactional
    public List<CertificateTemplateDTO> getTemplatesByCertificate(Integer issuerId) {
        List<CertificateTemplate> templates = repo.findByIssuerId(issuerId);
        return templates.stream()
                .map(t -> new CertificateTemplateDTO(
                        t.getId(),
                        t.getName(),
                        t.getTtlDays(),
                        t.getCommonNameRegex(),
                        t.getSubjectAltNameRegex(),
                        new ArrayList<>(t.getKeyUsage()),            // inicijalizuje lazy listu
                        new ArrayList<>(t.getExtendedKeyUsage())
                ))
                .collect(Collectors.toList());
    }

}


