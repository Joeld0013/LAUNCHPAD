package com.launchpad.admin.services;

import org.springframework.data.mongodb.core.query.Query;
import com.launchpad.admin.dto.*;
import com.launchpad.registration.model.StartupReg;
import com.launchpad.registration.model.DocumentFile;
import com.launchpad.admin.repository.DocumentFileRepository;
import com.launchpad.admin.repository.AdminStartupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StartupAdminService {

    private static final Logger logger = LoggerFactory.getLogger(StartupAdminService.class);

    @Autowired
    private AdminStartupRepository startupRepository;

    @Autowired
    private DocumentFileRepository documentRepository;

    @Autowired
    private AdminEmailService emailService;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Get all startups with optional filtering
     */
    public List<StartupResponseDTO> getAllStartups(StartupFilterDTO filter) {
        logger.info("Fetching startups with filter: {}", filter);

        Query query = new Query();

        if (filter != null) {
            if (filter.getIndustry() != null && !filter.getIndustry().isEmpty()) {
                query.addCriteria(Criteria.where("industry").regex(filter.getIndustry(), "i"));
            }

            if (filter.getStage() != null && !filter.getStage().isEmpty()) {
                query.addCriteria(Criteria.where("stage").regex(filter.getStage(), "i"));
            }

            if (filter.getCountry() != null && !filter.getCountry().isEmpty()) {
                query.addCriteria(Criteria.where("country").regex(filter.getCountry(), "i"));
            }

            if (filter.getRegistrationStatus() != null) {
                query.addCriteria(Criteria.where("registrationStatus").is(filter.getRegistrationStatus()));
            }

            if (filter.getSearchTerm() != null && !filter.getSearchTerm().isEmpty()) {
                Criteria searchCriteria = new Criteria().orOperator(
                        Criteria.where("name").regex(filter.getSearchTerm(), "i"),
                        Criteria.where("description").regex(filter.getSearchTerm(), "i"),
                        Criteria.where("industry").regex(filter.getSearchTerm(), "i")
                );
                query.addCriteria(searchCriteria);
            }
        }

        query.with(Sort.by(Sort.Direction.DESC, "createdAt"));

        List<StartupReg> startups = mongoTemplate.find(query, StartupReg.class);
        logger.info("Found {} startups", startups.size());

        return startups.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get startups by status
     */
    public List<StartupResponseDTO> getStartupsByStatus(String status) {
        logger.info("Fetching startups with status: {}", status);
        List<StartupReg> startups = startupRepository.findByRegistrationStatus(status);
        logger.info("Found {} startups with status {}", startups.size(), status);
        return startups.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get startup by ID with documents
     */
    public StartupResponseDTO getStartupById(String id) {
        logger.info("Fetching startup with ID: {}", id);
        StartupReg startup = startupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Startup not found with ID: " + id));
        return convertToDTO(startup);
    }

    /**
     * Approve a startup. Sends colored HTML email.
     */
    @Transactional
    public StartupResponseDTO approveStartup(ApprovalRequestDTO request) {
        logger.info("Approving startup with ID: {}", request.getStartupId());

        StartupReg startup = startupRepository.findById(request.getStartupId())
                .orElseThrow(() -> new RuntimeException("Startup not found with ID: " + request.getStartupId()));

        if ("APPROVED".equalsIgnoreCase(startup.getRegistrationStatus())) {
            throw new RuntimeException("Startup is already approved");
        }

        startup.setRegistrationStatus("APPROVED");
        startup.setIsVerified(true);
        startup.setApprovedAt(new Date());

        StartupReg savedStartup = startupRepository.save(startup);
        logger.info("Startup approved and saved: {}", savedStartup.getId());

        try {
            emailService.sendApprovalEmail(
                    savedStartup.getEmail(),
                    savedStartup.getContactPerson() != null ? savedStartup.getContactPerson() : savedStartup.getName(),
                    savedStartup.getName(),
                    "http://localhost:8080/startup_login.html"
            );
            logger.info("✅ Approval email sent to: {}", savedStartup.getEmail());
        } catch (Exception e) {
            logger.error("❌ Failed to send approval email: {}", e.getMessage(), e);
        }

        return convertToDTO(savedStartup);
    }

    /**
     * Reject a startup
     */
    @Transactional
    public StartupResponseDTO rejectStartup(ApprovalRequestDTO request) {
        logger.info("Rejecting startup with ID: {}", request.getStartupId());

        StartupReg startup = startupRepository.findById(request.getStartupId())
                .orElseThrow(() -> new RuntimeException("Startup not found with ID: " + request.getStartupId()));

        startup.setRegistrationStatus("REJECTED");
        startup.setIsVerified(false);

        StartupReg savedStartup = startupRepository.save(startup);

        try {
            emailService.sendRejectionEmail(
                    savedStartup.getEmail(),
                    savedStartup.getContactPerson() != null ? savedStartup.getContactPerson() : savedStartup.getName(),
                    savedStartup.getName(),
                    request.getComments()
            );
            logger.info("Rejection email sent to: {}", savedStartup.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send rejection email: {}", e.getMessage(), e);
        }

        return convertToDTO(savedStartup);
    }

    /**
     * Search startups
     */
    public List<StartupResponseDTO> searchStartups(String searchTerm) {
        logger.info("Searching startups with term: {}", searchTerm);
        List<StartupReg> startups = startupRepository.searchStartups(searchTerm);
        return startups.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get startup statistics
     */
    public StartupStatsDTO getStartupStatistics() {
        StartupStatsDTO stats = new StartupStatsDTO();
        stats.setTotalStartups(startupRepository.count());
        stats.setPendingApprovals(startupRepository.countByRegistrationStatus("PENDING"));
        stats.setApprovedStartups(startupRepository.countByRegistrationStatus("APPROVED"));
        stats.setRejectedStartups(startupRepository.countByRegistrationStatus("REJECTED"));
        return stats;
    }

    /**
     * Convert Startup entity to DTO
     */
    private StartupResponseDTO convertToDTO(StartupReg startup) {
        StartupResponseDTO dto = new StartupResponseDTO();

        dto.setId(startup.getId());
        dto.setName(startup.getName());
        dto.setEmail(startup.getEmail());
        dto.setPhone(startup.getPhone());
        dto.setCountry(startup.getCountry());
        dto.setAddress(startup.getAddress());
        dto.setIndustry(startup.getIndustry());
        dto.setStage(startup.getStage());
        dto.setDescription(startup.getDescription());
        dto.setRegistrationStatus(startup.getRegistrationStatus());
        dto.setIsVerified(startup.getIsVerified());

        if (startup.getCreatedAt() != null) {
            dto.setCreatedAt(startup.getCreatedAt());
        }

        if (startup.getApprovedAt() != null) {
            dto.setApprovedAt(startup.getApprovedAt());
        }

        dto.setWebsite(startup.getWebsite());
        dto.setBusinessPlan(startup.getBusinessPlan());
        dto.setFoundedDate(startup.getFoundedDate());
        dto.setTeamSize(startup.getTeamSize());
        dto.setContactPerson(startup.getContactPerson());

        List<DocumentDTO> documents = new ArrayList<>();
        try {
            logger.info("🔍 Looking for documents for startup ID: {}", startup.getId());

            List<DocumentFile> docFiles = documentRepository.findByStartupId(startup.getId());
            logger.info("🔎 Found {} documents for startup {}", docFiles.size(), startup.getId());

            if (docFiles.isEmpty()) {
                Query docQuery = new Query(Criteria.where("startupId").is(startup.getId()));
                docFiles = mongoTemplate.find(docQuery, DocumentFile.class, "documents");
                logger.info("🔎 MongoTemplate found {} documents", docFiles.size());
            }

            for (DocumentFile doc : docFiles) {
                logger.info("📄 Processing document: {} - {}", doc.getId(), doc.getFilePath());

                DocumentDTO docDTO = new DocumentDTO();
                docDTO.setId(doc.getId());

                String fileName = doc.getFilePath();
                if (fileName != null) {
                    if (fileName.contains("\\")) {
                        fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
                    } else if (fileName.contains("/")) {
                        fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                    }
                }
                docDTO.setFileName(fileName != null ? fileName : "Unknown");

                String fileType = doc.getDocType() != null ? doc.getDocType() : "OTHER";
                docDTO.setFileType(fileType);

                try {
                    if (doc.getFilePath() != null && !doc.getFilePath().isEmpty()) {
                        File file = new File(doc.getFilePath());
                        if (file.exists()) {
                            docDTO.setFileSize(file.length());
                            logger.info("✅ File exists: {} bytes", file.length());
                        } else {
                            docDTO.setFileSize(0L);
                            logger.warn("⚠️ File not found at path: {}", doc.getFilePath());
                        }
                    }
                } catch (Exception e) {
                    docDTO.setFileSize(0L);
                    logger.error("❌ Error reading file size: {}", e.getMessage());
                }

                if (doc.getUploadedAt() != null) {
                    docDTO.setUploadedAt(doc.getUploadedAt());
                }

                docDTO.setDownloadUrl("/api/admin/startups/documents/" + doc.getId() + "/download");
                documents.add(docDTO);

                logger.info("✅ Added document to DTO: {} (ID: {})", docDTO.getFileName(), docDTO.getId());
            }

            logger.info("📦 Total documents loaded: {}", documents.size());

        } catch (Exception e) {
            logger.error("❌ Error loading documents for startup {}: {}", startup.getId(), e.getMessage(), e);
            e.printStackTrace();
        }

        dto.setDocuments(documents);
        logger.info("📦 Total documents in final DTO: {}", documents.size());

        return dto;
    }

    /**
     * Helper method to convert registration.model.Startup to admin.model.Startup for emails
     */
    private com.launchpad.admin.model.Startup convertForEmail(StartupReg regStartup) {
        com.launchpad.admin.model.Startup adminStartup = new com.launchpad.admin.model.Startup();
        adminStartup.setId(regStartup.getId());
        adminStartup.setName(regStartup.getName());
        adminStartup.setEmail(regStartup.getEmail());
        adminStartup.setPhone(regStartup.getPhone());
        adminStartup.setCountry(regStartup.getCountry());
        adminStartup.setAddress(regStartup.getAddress());
        adminStartup.setIndustry(regStartup.getIndustry());
        adminStartup.setStage(regStartup.getStage());
        adminStartup.setDescription(regStartup.getDescription());
        adminStartup.setRegistrationStatus(regStartup.getRegistrationStatus());
        adminStartup.setIsVerified(regStartup.getIsVerified());
        adminStartup.setWebsite(regStartup.getWebsite());
        adminStartup.setContactPerson(regStartup.getContactPerson());

        if (regStartup.getCreatedAt() != null) {
            adminStartup.setCreatedAt(regStartup.getCreatedAt());
        }

        if (regStartup.getApprovedAt() != null) {
            adminStartup.setApprovedAt(regStartup.getApprovedAt());
        }

        return adminStartup;
    }
}
