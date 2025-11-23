package com.launchpad.admin.controller;

import com.launchpad.admin.services.AdminInvestorService;
import com.launchpad.admin.services.AdminFileService;
import com.launchpad.admin.model.InvestorAdmin;
import com.launchpad.admin.model.InvestorDocument;
import com.launchpad.admin.dto.InvestorDetailDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Investor Admin Controller
 * Handles all admin operations for investor management including:
 * - Viewing investor details
 * - Approving/Rejecting investors
 * - Managing investor documents
 */
@RestController
@RequestMapping("/api/admin/investors")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class InvestorAdminController {

    private static final Logger logger = LoggerFactory.getLogger(InvestorAdminController.class);

    @Autowired
    private AdminInvestorService investorService;

    @Autowired
    private AdminFileService fileService;

    // ========== GET ENDPOINTS ==========

    /**
     * Get all investors (pending + approved)
     * GET /api/admin/investors
     */
    @GetMapping
    public ResponseEntity<?> getAllInvestors() {
        try {
            logger.info("📥 GET /api/admin/investors - Fetching all investors");
            List<InvestorAdmin> investors = investorService.getAllInvestors();
            logger.info("✓ Found {} investors", investors.size());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", investors.size(),
                    "data", investors
            ));
        } catch (Exception e) {
            logger.error("❌ Error fetching all investors:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * Get pending investors only
     * GET /api/admin/investors/pending
     */
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingInvestors() {
        try {
            logger.info("📥 GET /api/admin/investors/pending - Fetching pending investors");
            List<InvestorAdmin> investors = investorService.getPendingInvestors();
            logger.info("✓ Found {} pending investors", investors.size());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", investors.size(),
                    "data", investors
            ));
        } catch (Exception e) {
            logger.error("❌ Error fetching pending investors:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * Get approved investors only
     * GET /api/admin/investors/approved
     */
    @GetMapping("/approved")
    public ResponseEntity<?> getApprovedInvestors() {
        try {
            logger.info("📥 GET /api/admin/investors/approved - Fetching approved investors");
            List<InvestorAdmin> investors = investorService.getApprovedInvestors();
            logger.info("✓ Found {} approved investors", investors.size());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "count", investors.size(),
                    "data", investors
            ));
        } catch (Exception e) {
            logger.error("❌ Error fetching approved investors:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * Get investor statistics
     * GET /api/admin/investors/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        try {
            logger.info("📥 GET /api/admin/investors/stats - Fetching statistics");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", Map.of(
                            "total", investorService.getTotalCount(),
                            "pending", investorService.getPendingCount(),
                            "approved", investorService.getApprovedCount()
                    )
            ));
        } catch (Exception e) {
            logger.error("❌ Error fetching stats:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * Get detailed investor information including documents
     * GET /api/admin/investors/{investorId}/details
     */
    @GetMapping("/{investorId}/details")
    public ResponseEntity<?> getInvestorDetails(@PathVariable String investorId) {
        try {
            logger.info("📥 GET /api/admin/investors/{}/details", investorId);

            InvestorDetailDTO details = investorService.getInvestorDetails(investorId);

            logger.info("✓ Investor details fetched for: {}", investorId);
            logger.info("📄 Documents found: {}", details.getDocuments() != null ? details.getDocuments().size() : 0);

            return ResponseEntity.ok(details);
        } catch (Exception e) {
            logger.error("❌ Error fetching investor details for {}: {}", investorId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    // ========== POST ENDPOINTS FOR APPROVAL/REJECTION ==========

    /**
     * Approve an investor
     * POST /api/admin/investors/{investorId}/approve
     */
    @PostMapping("/{investorId}/approve")
    public ResponseEntity<?> approveInvestor(@PathVariable String investorId) {
        try {
            logger.info("✅ POST /api/admin/investors/{}/approve", investorId);

            investorService.approveInvestor(investorId);

            logger.info("✓ Investor {} approved successfully", investorId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Investor approved successfully"
            ));
        } catch (Exception e) {
            logger.error("❌ Error approving investor {}: {}", investorId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * Reject an investor with reason
     * POST /api/admin/investors/{investorId}/reject?reason=...
     */
    @PostMapping("/{investorId}/reject")
    public ResponseEntity<?> rejectInvestor(
            @PathVariable String investorId,
            @RequestParam(required = false, defaultValue = "Rejected by admin") String reason) {
        try {
            logger.info("❌ POST /api/admin/investors/{}/reject - Reason: {}", investorId, reason);

            investorService.rejectInvestor(investorId, reason);

            logger.info("✓ Investor {} rejected successfully", investorId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Investor rejected successfully"
            ));
        } catch (Exception e) {
            logger.error("❌ Error rejecting investor {}: {}", investorId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    // ========== DOCUMENT ENDPOINTS ==========

    /**
     * Download a document
     * GET /api/admin/investors/document/{documentId}/download
     */
    @GetMapping("/document/{documentId}/download")
    public ResponseEntity<?> downloadDocument(@PathVariable String documentId) {
        try {
            logger.info("⬇️ GET /api/admin/investors/document/{}/download", documentId);

            InvestorDocument doc = fileService.getInvestorDocumentInfo(documentId);
            byte[] fileContent = fileService.downloadInvestorDocument(documentId);

            logger.info("✓ Downloaded document: {} ({} bytes)", doc.getFileName(), fileContent.length);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + doc.getFileName() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, fileService.getContentType(doc.getFileType()))
                    .body(fileContent);
        } catch (Exception e) {
            logger.error("❌ Error downloading document {}: {}", documentId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * View a document in browser
     * GET /api/admin/investors/document/{documentId}/view
     */
    @GetMapping("/document/{documentId}/view")
    public ResponseEntity<?> viewDocument(@PathVariable String documentId) {
        try {
            logger.info("👁️ GET /api/admin/investors/document/{}/view", documentId);

            InvestorDocument doc = fileService.getInvestorDocumentInfo(documentId);
            byte[] fileContent = fileService.downloadInvestorDocument(documentId);

            logger.info("✓ Viewing document: {}", doc.getFileName());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, fileService.getContentType(doc.getFileType()))
                    .body(fileContent);
        } catch (Exception e) {
            logger.error("❌ Error viewing document {}: {}", documentId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * Delete a document
     * DELETE /api/admin/investors/document/{documentId}
     */
    @DeleteMapping("/document/{documentId}")
    public ResponseEntity<?> deleteDocument(@PathVariable String documentId) {
        try {
            logger.info("🗑️ DELETE /api/admin/investors/document/{}", documentId);

            fileService.deleteInvestorDocument(documentId);

            logger.info("✓ Document {} deleted successfully", documentId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Document deleted successfully"
            ));
        } catch (Exception e) {
            logger.error("❌ Error deleting document {}: {}", documentId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    // ========== OPTIONS FOR CORS PREFLIGHT ==========

    /**
     * Handle CORS preflight requests
     */
    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<?> handleOptions() {
        logger.debug("🔀 OPTIONS request received");
        return ResponseEntity.ok().build();
    }

    /**
     * Handle CORS preflight for all paths
     */
    @RequestMapping(path = "/**", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> handleAllOptions() {
        logger.debug("🔀 OPTIONS request received for all paths");
        return ResponseEntity.ok().build();
    }
}