package com.launchpad.admin.services;

import com.launchpad.admin.dto.*;
import com.launchpad.registration.model.Startup;  // Use registration model
import com.launchpad.registration.model.DocumentFile;  // Use registration model
import com.launchpad.admin.repository.StartupDocumentRepository;
import com.launchpad.admin.repository.AdminStartupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
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
    private StartupDocumentRepository documentRepository;

    @Autowired
    private AdminEmailService emailService;

    @Autowired
    private MongoTemplate mongoTemplate;

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
                query.addCriteria(Criteria.where("registrationStatus").is(filter.getRegistrationStatus().toString()));
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

        List<Startup> startups = mongoTemplate.find(query, Startup.class);
        logger.info("Found {} startups", startups.size());

        return startups.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<StartupResponseDTO> getStartupsByStatus(String status) {
        logger.info("Fetching startups with status: {}", status);
        List<Startup> startups = startupRepository.findByRegistrationStatus(status);
        logger.info("Found {} startups with status {}", startups.size(), status);
        return startups.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public StartupResponseDTO getStartupById(String id) {
        logger.info("Fetching startup with ID: {}", id);
        Startup startup = startupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Startup not found with ID: " + id));
        return convertToDTO(startup);
    }

    @Transactional
    public StartupResponseDTO approveStartup(ApprovalRequestDTO request) {
        logger.info("Approving startup with ID: {}", request.getStartupId());

        Startup startup = startupRepository.findById(request.getStartupId())
                .orElseThrow(() -> new RuntimeException("Startup not found with ID: " + request.getStartupId()));

        if ("APPROVED".equalsIgnoreCase(startup.getRegistrationStatus())) {
            throw new RuntimeException("Startup is already approved");
        }

        startup.setRegistrationStatus("APPROVED");
        startup.setIsVerified(true);

        Startup savedStartup = startupRepository.save(startup);
        logger.info("Startup approved and saved: {}", savedStartup.getId());

        try {
            // Create temp Startup object for email (since AdminEmailService expects admin.model.Startup)
            com.launchpad.admin.model.Startup emailStartup = convertForEmail(savedStartup);
            emailService.sendApprovalEmail(emailStartup);
            logger.info("Approval email sent to: {}", savedStartup.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send approval email: {}", e.getMessage());
        }

        return convertToDTO(savedStartup);
    }

    @Transactional
    public StartupResponseDTO rejectStartup(ApprovalRequestDTO request) {
        logger.info("Rejecting startup with ID: {}", request.getStartupId());

        Startup startup = startupRepository.findById(request.getStartupId())
                .orElseThrow(() -> new RuntimeException("Startup not found with ID: " + request.getStartupId()));

        startup.setRegistrationStatus("REJECTED");
        startup.setIsVerified(false);

        Startup savedStartup = startupRepository.save(startup);

        try {
            com.launchpad.admin.model.Startup emailStartup = convertForEmail(savedStartup);
            emailService.sendRejectionEmail(emailStartup, request.getComments());
            logger.info("Rejection email sent to: {}", savedStartup.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send rejection email: {}", e.getMessage());
        }

        return convertToDTO(savedStartup);
    }

    public List<StartupResponseDTO> searchStartups(String searchTerm) {
        logger.info("Searching startups with term: {}", searchTerm);
        List<Startup> startups = startupRepository.searchStartups(searchTerm);
        return startups.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public StartupStatsDTO getStartupStatistics() {
        StartupStatsDTO stats = new StartupStatsDTO();
        stats.setTotalStartups(startupRepository.count());
        stats.setPendingApprovals(startupRepository.countByRegistrationStatus("PENDING"));
        stats.setApprovedStartups(startupRepository.countByRegistrationStatus("APPROVED"));
        stats.setRejectedStartups(startupRepository.countByRegistrationStatus("REJECTED"));
        return stats;
    }

    private StartupResponseDTO convertToDTO(Startup startup) {
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
        dto.setIsVerified(startup.isVerified());

        // Convert LocalDateTime to Date for DTO
        if (startup.getCreatedAt() != null) {
            dto.setCreatedAt(Date.from(startup.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()));
        }

        dto.setWebsite(startup.getWebsite());
        dto.setBusinessPlan(null);  // Not in registration model
        dto.setFoundedDate(null);   // Not in registration model
        dto.setTeamSize(null);      // Not in registration model
        dto.setContactPerson(startup.getName());  // Use name as contact person

        // Load documents
        if (startup.getDocumentIds() != null && !startup.getDocumentIds().isEmpty()) {
            List<DocumentDTO> documents = new ArrayList<>();
            for (String docId : startup.getDocumentIds()) {
                documentRepository.findById(docId).ifPresent(doc -> {
                    DocumentDTO docDTO = new DocumentDTO();
                    docDTO.setId(doc.getId());
                    docDTO.setFileName(doc.getFilePath().substring(doc.getFilePath().lastIndexOf("\\") + 1));
                    docDTO.setFileType(doc.getDocType().toString());
                    docDTO.setFileSize(0L);  // File size not stored in DocumentFile

                    if (doc.getUploadedAt() != null) {
                        docDTO.setUploadedAt(doc.getUploadedAt());
                    }

                    docDTO.setDownloadUrl("/api/admin/startups/documents/" + doc.getId() + "/download");
                    documents.add(docDTO);
                });
            }
            dto.setDocuments(documents);
        } else {
            dto.setDocuments(new ArrayList<>());
        }

        return dto;
    }

    // Helper method to convert registration.model.Startup to admin.model.Startup for emails
    private com.launchpad.admin.model.Startup convertForEmail(Startup regStartup) {
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
        adminStartup.setIsVerified(regStartup.isVerified());
        adminStartup.setWebsite(regStartup.getWebsite());
        adminStartup.setContactPerson(regStartup.getName());

        if (regStartup.getCreatedAt() != null) {
            adminStartup.setApprovedAt(Date.from(regStartup.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()));
        }

        return adminStartup;
    }
}