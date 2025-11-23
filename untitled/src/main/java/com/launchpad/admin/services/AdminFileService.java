package com.launchpad.admin.services;

import com.launchpad.admin.model.InvestorDocument;
import com.launchpad.registration.model.DocumentFile;
import com.launchpad.admin.repository.InvestorDocumentRepository;
import com.launchpad.admin.repository.DocumentFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * File Service for handling document operations
 * Supports both investor and startup documents
 */
@Service
public class AdminFileService {

    private static final Logger logger = LoggerFactory.getLogger(AdminFileService.class);

    @Autowired
    private InvestorDocumentRepository investorDocumentRepository;

    @Autowired
    private DocumentFileRepository startupDocumentRepository;

    // ========== INVESTOR DOCUMENT METHODS ==========

    /**
     * Download investor document as bytes
     */
    public byte[] downloadInvestorDocument(String documentId) throws IOException {
        logger.info("⬇️ Downloading investor document: {}", documentId);

        InvestorDocument doc = investorDocumentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Investor document not found"));

        File file = new File(doc.getFilePath());
        if (!file.exists()) {
            logger.error("❌ File not found at path: {}", doc.getFilePath());
            throw new IOException("File not found at path: " + doc.getFilePath());
        }

        byte[] fileBytes = Files.readAllBytes(Paths.get(doc.getFilePath()));
        logger.info("✓ Successfully read {} bytes", fileBytes.length);
        return fileBytes;
    }

    /**
     * Get investor document information
     */
    public InvestorDocument getInvestorDocumentInfo(String documentId) {
        logger.info("📄 Fetching investor document info: {}", documentId);

        InvestorDocument doc = investorDocumentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Investor document not found"));

        logger.info("✓ Document found: {}", doc.getFileName());
        return doc;
    }

    /**
     * Delete investor document
     */
    public void deleteInvestorDocument(String documentId) throws IOException {
        logger.info("🗑️ Deleting investor document: {}", documentId);

        InvestorDocument doc = investorDocumentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Investor document not found"));

        Files.deleteIfExists(Paths.get(doc.getFilePath()));
        investorDocumentRepository.deleteById(documentId);

        logger.info("✓ Investor document deleted: {}", documentId);
    }

    // ========== STARTUP DOCUMENT METHODS ==========

    /**
     * Download startup document as bytes
     */
    public byte[] downloadStartupDocument(String documentId) throws IOException {
        logger.info("⬇️ Downloading startup document: {}", documentId);

        DocumentFile doc = startupDocumentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Startup document not found"));

        File file = new File(doc.getFilePath());
        if (!file.exists()) {
            logger.error("❌ File not found at path: {}", doc.getFilePath());
            throw new IOException("File not found at path: " + doc.getFilePath());
        }

        byte[] fileBytes = Files.readAllBytes(Paths.get(doc.getFilePath()));
        logger.info("✓ Successfully read {} bytes", fileBytes.length);
        return fileBytes;
    }

    /**
     * Get startup document information
     */
    public DocumentFile getStartupDocumentInfo(String documentId) {
        logger.info("📄 Fetching startup document info: {}", documentId);

        DocumentFile doc = startupDocumentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Startup document not found"));

        logger.info("✓ Document found: {}", doc.getFilePath());
        return doc;
    }

    /**
     * Delete startup document
     */
    public void deleteStartupDocument(String documentId) throws IOException {
        logger.info("🗑️ Deleting startup document: {}", documentId);

        DocumentFile doc = startupDocumentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Startup document not found"));

        Files.deleteIfExists(Paths.get(doc.getFilePath()));
        startupDocumentRepository.deleteById(documentId);

        logger.info("✓ Startup document deleted: {}", documentId);
    }

    // ========== UTILITY METHODS ==========

    /**
     * Get content type based on file extension
     */
    public String getContentType(String fileType) {
        if (fileType == null) return "application/octet-stream";

        return switch (fileType.toLowerCase()) {
            case "pdf" -> "application/pdf";
            case "doc", "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls", "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "zip" -> "application/zip";
            case "txt" -> "text/plain";
            case "csv" -> "text/csv";
            default -> "application/octet-stream";
        };
    }

    /**
     * Get file extension from file type
     */
    public String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "unknown";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }
}