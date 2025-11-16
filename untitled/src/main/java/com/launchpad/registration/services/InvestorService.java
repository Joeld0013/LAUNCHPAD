package com.launchpad.registration.services;

import com.launchpad.registration.dto.InvestorRegistrationRequest;
import com.launchpad.registration.model.DocumentFile;
import com.launchpad.registration.model.DocType;
import com.launchpad.registration.model.DocumentStatus;
import com.launchpad.registration.model.InvestorReg;
import com.launchpad.registration.repository.DocumentRepository;
import com.launchpad.registration.repository.InvestorRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InvestorService {

    @Autowired
    private InvestorRepository investorRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private InvestorEmailService investorEmailService;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public InvestorReg registerInvestor(InvestorRegistrationRequest req, List<MultipartFile> documents) throws Exception {
        // Check duplicate email
        Optional<InvestorReg> exists = investorRepository.findByEmail(req.getEmail());
        if (exists.isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        InvestorReg investor = new InvestorReg();
        investor.setName(req.getName());
        investor.setEmail(req.getEmail());
        investor.setPhone(req.getPhone());
        investor.setCountry(req.getCountry());
        investor.setOrganization(req.getOrganization());
        investor.setAddress(req.getAddress());
        investor.setInvestorType(req.getInvestorType());
        investor.setPreferences(req.getPreferences());
        investor.setRegistrationStatus("PENDING");

        // Hash and store password immediately during registration
        if (req.getPassword() != null && !req.getPassword().trim().isEmpty()) {
            String hashedPassword = passwordEncoder.encode(req.getPassword());
            investor.setPasswordHash(hashedPassword);
        }

        InvestorReg saved = investorRepository.save(investor);

        List<String> docIds = new ArrayList<>();
        if (documents != null && !documents.isEmpty()) {
            // Create upload directory
            Path base = Paths.get(uploadDir, "investors", saved.getId());
            Files.createDirectories(base);

            // Parse docType from request
            DocType docTypeEnum = parseDocType(req.getDocType());

            for (MultipartFile file : documents) {
                if (file == null || file.isEmpty()) continue;

                String ext = FilenameUtils.getExtension(file.getOriginalFilename());
                String filename = UUID.randomUUID().toString() + (ext != null && !ext.isEmpty() ? "." + ext : "");
                Path target = base.resolve(filename);
                Files.copy(file.getInputStream(), target);

                DocumentFile doc = new DocumentFile();
                doc.setStartupId(saved.getId()); // Reusing field name for investor ID
                doc.setDocType(String.valueOf(docTypeEnum));
                doc.setFilePath(target.toString());
                doc.setStatus(String.valueOf(DocumentStatus.PENDING));
                DocumentFile savedDoc = documentRepository.save(doc);
                docIds.add(savedDoc.getId());
            }
        }

        saved.setDocumentIds(docIds);
        investorRepository.save(saved);

        // Send pending email notification
        try {
            investorEmailService.sendPendingEmail(saved.getEmail(), saved.getName(), saved.getName());
        } catch (Exception ex) {
            System.err.println("Failed to send email: " + ex.getMessage());
        }

        return saved;
    }

    // Helper method to parse document type
    private DocType parseDocType(String docTypeStr) {
        if (docTypeStr == null || docTypeStr.trim().isEmpty()) {
            return DocType.OTHER;
        }

        try {
            switch (docTypeStr.toLowerCase()) {
                case "identity_proof":
                    return DocType.IDENTITY_PROOF;
                case "accreditation":
                case "tax_cert":
                    return DocType.TAX_CERT;
                case "financial":
                case "business_reg":
                    return DocType.BUSINESS_REG;
                default:
                    return DocType.OTHER;
            }
        } catch (Exception e) {
            return DocType.OTHER;
        }
    }

    public InvestorReg approveInvestor(String investorId) {
        InvestorReg investor = investorRepository.findById(investorId)
                .orElseThrow(() -> new IllegalArgumentException("Investor not found"));

        if ("APPROVED".equalsIgnoreCase(investor.getRegistrationStatus())) {
            return investor;
        }

        String rawPassword = null;
        // Generate new password on approval
        rawPassword = generateRandomPassword(12);
        String hashed = passwordEncoder.encode(rawPassword);
        investor.setPasswordHash(hashed);

        investor.setRegistrationStatus("APPROVED");
        investor.setVerified(true);
        investor.setApprovedAt(new Date());
        investor.setUpdatedAt(new Date());

        InvestorReg saved = investorRepository.save(investor);

        // Send credentials via email
        investorEmailService.sendApprovalEmail(
                saved.getEmail(),
                saved.getName(),
                saved.getName(),
                "http://localhost:8080/investor_login.html",
                rawPassword
        );

        return saved;
    }

    public InvestorReg rejectInvestor(String investorId, String reason) {
        InvestorReg investor = investorRepository.findById(investorId)
                .orElseThrow(() -> new IllegalArgumentException("Investor not found"));

        investor.setRegistrationStatus("REJECTED");
        investor.setUpdatedAt(new Date());
        InvestorReg saved = investorRepository.save(investor);

        // Send rejection email
        try {
            investorEmailService.sendRejectedEmail(saved.getEmail(), saved.getName(), saved.getName());
        } catch (Exception ex) {
            System.err.println("Failed to send rejection email: " + ex.getMessage());
        }

        return saved;
    }

    public List<InvestorReg> getAllInvestors() {
        return investorRepository.findAll();
    }

    private String generateRandomPassword(int length) {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%&*()-_=+";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
}