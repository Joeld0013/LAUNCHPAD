package com.launchpad.registration.services;

import com.launchpad.registration.dto.StartupRegistrationRequest;
import com.launchpad.registration.model.DocumentFile;
import com.launchpad.registration.model.DocType;
import com.launchpad.registration.model.DocumentStatus;
import com.launchpad.registration.model.Startup;
import com.launchpad.registration.repository.DocumentRepository;
import com.launchpad.registration.repository.StartupRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StartupService {

    @Autowired
    private StartupRepository startupRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private EmailService emailService;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Startup registerStartup(StartupRegistrationRequest req, List<MultipartFile> documents) throws Exception {
        // check duplicate email
        Optional<Startup> exists = startupRepository.findByEmail(req.getEmail());
        if (exists.isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        Startup s = new Startup();
        s.setName(req.getName());
        s.setEmail(req.getEmail());
        s.setPhone(req.getPhone());
        s.setCountry(req.getCountry());
        s.setAddress(req.getAddress());
        s.setIndustry(req.getIndustry());
        s.setStage(req.getStage());
        s.setDescription(req.getDescription());
        s.setWebsite(req.getWebsite());
        s.setRegistrationStatus("PENDING"); // String, not enum

        // Hash and store password immediately during registration
        if (req.getPassword() != null && !req.getPassword().trim().isEmpty()) {
            String hashedPassword = passwordEncoder.encode(req.getPassword());
            s.setPasswordHash(hashedPassword);
        }

        Startup saved = startupRepository.save(s);

        List<String> docIds = new ArrayList<>();
        if (documents != null && !documents.isEmpty()) {
            // Create upload directory
            Path base = Paths.get(uploadDir, "startups", saved.getId());
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
                doc.setStartupId(saved.getId());
                doc.setDocType(docTypeEnum);
                doc.setFilePath(target.toString());
                doc.setStatus(DocumentStatus.PENDING);
                DocumentFile savedDoc = documentRepository.save(doc);
                docIds.add(savedDoc.getId());
            }
        }

        saved.setDocumentIds(docIds);
        startupRepository.save(saved);

        // Send pending email notification
        try {
            emailService.sendPendingEmail(saved.getEmail());
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
                case "business_reg":
                    return DocType.BUSINESS_REG;
                case "tax_cert":
                    return DocType.TAX_CERT;
                case "aadhaar":
                    return DocType.AADHAAR;
                case "identity_proof":
                    return DocType.IDENTITY_PROOF;
                default:
                    return DocType.OTHER;
            }
        } catch (Exception e) {
            return DocType.OTHER;
        }
    }

    // Modified: Don't generate new password if one already exists
    public Startup approveStartup(String startupId) {
        Startup s = startupRepository.findById(startupId)
                .orElseThrow(() -> new IllegalArgumentException("Startup not found"));
        if ("APPROVED".equalsIgnoreCase(s.getRegistrationStatus())) {
            return s;
        }

        String rawPassword = null;
        if (s.getPasswordHash() == null || s.getPasswordHash().trim().isEmpty()) {
            rawPassword = generateRandomPassword(12);
            String hashed = passwordEncoder.encode(rawPassword);
            s.setPasswordHash(hashed);
        } else {
            // Optionally, generate a temp password or just send info
            rawPassword = generateRandomPassword(12);
            String hashed = passwordEncoder.encode(rawPassword);
            s.setPasswordHash(hashed);
        }

        s.setRegistrationStatus("APPROVED"); // String, not enum
        s.setVerified(true);

        Startup saved = startupRepository.save(s);

        // Send credentials via email
        emailService.sendApprovalEmail(saved.getEmail(), rawPassword);
        return saved;
    }

    public Startup rejectStartup(String startupId, String reason) {
        Startup s = startupRepository.findById(startupId)
                .orElseThrow(() -> new IllegalArgumentException("Startup not found"));
        s.setRegistrationStatus("REJECTED"); // String, not enum
        Startup saved = startupRepository.save(s);
        // optionally send rejection email (omitted here)
        return saved;
    }

    // Add method to get all startups for admin
    public List<Startup> getAllStartups() {
        return startupRepository.findAll();
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
