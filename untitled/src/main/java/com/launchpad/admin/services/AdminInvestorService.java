package com.launchpad.admin.services;

import com.launchpad.admin.model.InvestorAdmin;
import com.launchpad.admin.model.InvestorDocument;
import com.launchpad.admin.repository.AdminInvestorRepository;
import com.launchpad.admin.repository.InvestorDocumentRepository;
import com.launchpad.admin.dto.InvestorDetailDTO;
import com.launchpad.admin.dto.InvestorDocumentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin Investor Service
 * Handles business logic for investor management
 */
@Service("adminInvestorService")
public class AdminInvestorService {

    private static final Logger logger = LoggerFactory.getLogger(AdminInvestorService.class);

    @Autowired
    private AdminInvestorRepository investorRepository;

    @Autowired
    private InvestorDocumentRepository documentRepository;

    // ========== GET METHODS ==========

    /**
     * Get all investors
     */
    public List<InvestorAdmin> getAllInvestors() {
        logger.info("📥 Fetching all investors");
        List<InvestorAdmin> investors = investorRepository.findAll();
        logger.info("✓ Found {} investors", investors.size());
        return investors;
    }

    /**
     * Get pending investors only
     */
    public List<InvestorAdmin> getPendingInvestors() {
        logger.info("📥 Fetching pending investors");
        List<InvestorAdmin> investors = investorRepository.findByRegistrationStatus("PENDING");
        logger.info("✓ Found {} pending investors", investors.size());
        return investors;
    }

    /**
     * Get approved investors only
     */
    public List<InvestorAdmin> getApprovedInvestors() {
        logger.info("📥 Fetching approved investors");
        List<InvestorAdmin> investors = investorRepository.findByRegistrationStatus("APPROVED");
        logger.info("✓ Found {} approved investors", investors.size());
        return investors;
    }

    /**
     * Get investor details with documents
     */
    public InvestorDetailDTO getInvestorDetails(String investorId) {
        logger.info("📥 Fetching investor details for ID: {}", investorId);

        InvestorAdmin investor = investorRepository.findById(investorId)
                .orElseThrow(() -> {
                    logger.error("❌ Investor not found: {}", investorId);
                    return new RuntimeException("Investor not found with ID: " + investorId);
                });

        logger.info("✓ Investor found: {}", investor.getName());

        InvestorDetailDTO dto = new InvestorDetailDTO();
        dto.set_id(investor.get_id());
        dto.setName(investor.getName());
        dto.setEmail(investor.getEmail());
        dto.setPhone(investor.getPhone());
        dto.setCountry(investor.getCountry());
        dto.setOrganization(investor.getOrganization());
        dto.setAddress(investor.getAddress());
        dto.setInvestorType(investor.getInvestorType());
        dto.setPreferences(investor.getPreferences());
        dto.setRegistrationStatus(investor.getRegistrationStatus());
        dto.setVerified(investor.isVerified());
        dto.setCreatedAt(investor.getCreatedAt());
        dto.setInvestmentRange(investor.getInvestmentRange());
        dto.setPastInvestments(investor.getPastInvestments());
        dto.setRejectionReason(investor.getRejectionReason());

        // Fetch documents
        logger.info("📄 Fetching documents for investor: {}", investorId);
        List<InvestorDocument> documents = documentRepository.findByInvestorId(investorId);
        logger.info("✓ Found {} documents", documents.size());

        List<InvestorDocumentDTO> documentDTOs = documents.stream()
                .map(doc -> new InvestorDocumentDTO(
                        doc.get_id(),
                        doc.getFileName(),
                        doc.getFileType(),
                        doc.getFileSize(),
                        doc.getUploadedAt(),
                        doc.getDocumentCategory()
                ))
                .collect(Collectors.toList());

        logger.info("✓ Documents converted to DTOs: {}", documentDTOs.size());
        dto.setDocuments(documentDTOs);

        return dto;
    }

    // ========== ACTION METHODS ==========

    /**
     * Approve investor
     */
    public void approveInvestor(String investorId) {
        logger.info("✅ Approving investor: {}", investorId);

        InvestorAdmin investor = investorRepository.findById(investorId)
                .orElseThrow(() -> {
                    logger.error("❌ Investor not found: {}", investorId);
                    return new RuntimeException("Investor not found");
                });

        investor.setRegistrationStatus("APPROVED");
        investor.setVerified(true);
        investor.setVerificationDate(LocalDateTime.now());
        investor.setUpdatedAt(LocalDateTime.now());

        investorRepository.save(investor);

        logger.info("✓ Investor {} approved successfully", investorId);
    }

    /**
     * Reject investor with reason
     */
    public void rejectInvestor(String investorId, String reason) {
        logger.info("❌ Rejecting investor: {} - Reason: {}", investorId, reason);

        InvestorAdmin investor = investorRepository.findById(investorId)
                .orElseThrow(() -> {
                    logger.error("❌ Investor not found: {}", investorId);
                    return new RuntimeException("Investor not found");
                });

        investor.setRegistrationStatus("REJECTED");
        investor.setRejectionReason(reason);
        investor.setUpdatedAt(LocalDateTime.now());

        investorRepository.save(investor);

        logger.info("✓ Investor {} rejected successfully", investorId);
    }

    // ========== STATISTICS METHODS ==========

    /**
     * Get pending count
     */
    public long getPendingCount() {
        long count = investorRepository.countByRegistrationStatus("PENDING");
        logger.info("📊 Pending investors count: {}", count);
        return count;
    }

    /**
     * Get approved count
     */
    public long getApprovedCount() {
        long count = investorRepository.countByRegistrationStatus("APPROVED");
        logger.info("📊 Approved investors count: {}", count);
        return count;
    }

    /**
     * Get total count
     */
    public long getTotalCount() {
        long count = investorRepository.count();
        logger.info("📊 Total investors count: {}", count);
        return count;
    }
}