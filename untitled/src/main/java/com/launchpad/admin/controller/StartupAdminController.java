package com.launchpad.admin.controller;

import com.launchpad.registration.model.StartupReg;
import com.launchpad.registration.model.DocumentFile;
import com.launchpad.registration.repository.StartupRepository;
import com.launchpad.registration.services.EmailService;
import com.launchpad.admin.repository.DocumentFileRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.file.Files;
import java.util.*;
import java.text.SimpleDateFormat;
import java.io.File;

@RestController
@RequestMapping("/api/admin/startups")
@CrossOrigin(origins = "*")
public class StartupAdminController {

    @Autowired
    private StartupRepository startupRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DocumentFileRepository documentFileRepository;

    // Get all startups with filters
    @GetMapping
    public ResponseEntity<?> getStartups(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String stage,
            @RequestParam(required = false) String country) {

        try {
            Query query = new Query();

            // Add status filter
            if (status != null && !status.isEmpty()) {
                query.addCriteria(Criteria.where("registrationStatus").is(status));
            }

            // Add search filter - searches in name, description, and industry
            if (search != null && !search.isEmpty()) {
                Criteria searchCriteria = new Criteria().orOperator(
                        Criteria.where("name").regex(search, "i"),
                        Criteria.where("description").regex(search, "i"),
                        Criteria.where("industry").regex(search, "i")
                );
                query.addCriteria(searchCriteria);
            }

            // Add industry filter
            if (industry != null && !industry.isEmpty()) {
                query.addCriteria(Criteria.where("industry").regex(industry, "i"));
            }

            // Add stage filter
            if (stage != null && !stage.isEmpty()) {
                query.addCriteria(Criteria.where("stage").regex(stage, "i"));
            }

            // Add country filter
            if (country != null && !country.isEmpty()) {
                query.addCriteria(Criteria.where("country").regex(country, "i"));
            }

            List<StartupReg> startups = mongoTemplate.find(query, StartupReg.class);

            System.out.println("Search query: " + search);
            System.out.println("Found startups: " + startups.size());

            return ResponseEntity.ok(Map.of("success", true, "data", startups));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // Get startup details WITH documents
    @GetMapping("/{id}")
    public ResponseEntity<?> getStartupDetail(@PathVariable String id) {
        try {
            Optional<StartupReg> startupOpt = startupRepo.findById(id);
            if (startupOpt.isEmpty())
                return ResponseEntity.status(404).body(Map.of("success", false, "message", "Startup not found"));

            StartupReg s = startupOpt.get();

            // GET DOCUMENTS FOR THIS STARTUP
            List<DocumentFile> documents = documentFileRepository.findByStartupId(id);
            System.out.println("📄 Found " + documents.size() + " documents for startup: " + id);

            // ADD DOCUMENTS TO STARTUP OBJECT
            Map<String, Object> startupData = new HashMap<>();
            startupData.put("id", s.getId());
            startupData.put("name", s.getName());
            startupData.put("email", s.getEmail());
            startupData.put("phone", s.getPhone());
            startupData.put("country", s.getCountry());
            startupData.put("address", s.getAddress());
            startupData.put("industry", s.getIndustry());
            startupData.put("stage", s.getStage());
            startupData.put("description", s.getDescription());
            startupData.put("website", s.getWebsite());
            startupData.put("businessPlan", s.getBusinessPlan());
            startupData.put("foundedDate", s.getFoundedDate());
            startupData.put("teamSize", s.getTeamSize());
            startupData.put("contactPerson", s.getContactPerson());
            startupData.put("registrationStatus", s.getRegistrationStatus());
            startupData.put("isVerified", s.getIsVerified());
            startupData.put("createdAt", s.getCreatedAt());
            startupData.put("documents", documents);  // ADD DOCUMENTS HERE

            return ResponseEntity.ok(Map.of("success", true, "data", startupData));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // Debug endpoint to check documents
    @GetMapping("/debug/{startupId}/documents")
    public ResponseEntity<?> debugDocuments(@PathVariable String startupId) {
        try {
            System.out.println("🔍 DEBUG: Looking for documents for startup ID: " + startupId);

            // Check if startup exists
            Optional<StartupReg> startupOpt = startupRepo.findById(startupId);
            if (startupOpt.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of(
                        "success", false,
                        "message", "Startup not found with ID: " + startupId
                ));
            }

            // Query documents
            List<DocumentFile> docs = documentFileRepository.findByStartupId(startupId);
            System.out.println("🔎 DEBUG: Found " + docs.size() + " documents");

            // Build detailed response
            List<Map<String, Object>> docDetails = new ArrayList<>();
            for (DocumentFile doc : docs) {
                Map<String, Object> detail = new HashMap<>();
                detail.put("id", doc.getId());
                detail.put("startupId", doc.getStartupId());
                detail.put("filePath", doc.getFilePath());
                detail.put("docType", doc.getDocType() != null ? doc.getDocType().toString() : "null");
                detail.put("uploadedAt", doc.getUploadedAt());
                detail.put("status", doc.getStatus());

                // Check if file exists on disk
                if (doc.getFilePath() != null) {
                    File file = new File(doc.getFilePath());
                    detail.put("fileExists", file.exists());
                    if (file.exists()) {
                        detail.put("fileSize", file.length());
                        detail.put("fileName", file.getName());
                    }
                } else {
                    detail.put("fileExists", false);
                    detail.put("note", "filePath is null");
                }

                docDetails.add(detail);
                System.out.println("📄 DEBUG: Document - " + detail);
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "startupId", startupId,
                    "startupName", startupOpt.get().getName(),
                    "documentsFound", docs.size(),
                    "documents", docDetails
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "error", e.getMessage(),
                    "stackTrace", Arrays.toString(e.getStackTrace())
            ));
        }
    }

    // Verify documents are accessible
    @GetMapping("/verify/{startupId}")
    public ResponseEntity<?> verify(@PathVariable String startupId) {
        try {
            List<DocumentFile> docs = documentFileRepository.findByStartupId(startupId);

            Map<String, Object> response = new HashMap<>();
            response.put("startupId", startupId);
            response.put("documentsFound", docs.size());
            response.put("documents", docs);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // Approve a startup
    @PostMapping("/approve")
    public ResponseEntity<?> approveStartup(@RequestBody Map<String, Object> body) {
        String startupId = (String) body.get("startupId");
        Optional<StartupReg> startupOpt = startupRepo.findById(startupId);
        if (startupOpt.isEmpty())
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Startup not found"));
        StartupReg s = startupOpt.get();
        s.setRegistrationStatus("APPROVED");
        s.setIsVerified(true);
        s.setApprovedAt(new Date());
        startupRepo.save(s);

        try {
            emailService.sendApprovedEmail(s.getEmail(), s.getName());
            System.out.println("✅ Email sent to: " + s.getEmail());
        } catch (Exception e) {
            System.err.println("❌ Email send error: " + e.getMessage());
            e.printStackTrace();
        }

        return ResponseEntity.ok(Map.of("success", true, "message", "Startup approved successfully"));
    }

    // Reject a startup
    @PostMapping("/reject")
    public ResponseEntity<?> rejectStartup(@RequestBody Map<String, Object> body) {
        String startupId = (String) body.get("startupId");
        String comments = (String) body.getOrDefault("comments", "Rejected by admin.");
        Optional<StartupReg> startupOpt = startupRepo.findById(startupId);
        if (startupOpt.isEmpty())
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Startup not found"));
        StartupReg s = startupOpt.get();
        s.setRegistrationStatus("REJECTED");
        s.setIsVerified(false);
        startupRepo.save(s);

        try {
            emailService.sendRejectedEmail(s.getEmail(), s.getName(), comments);
        } catch (Exception e) {
            System.err.println("Email send error: " + e.getMessage());
        }

        return ResponseEntity.ok(Map.of("success", true, "message", "Startup rejected"));
    }

    // Get statistics
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        long pendingApprovals = startupRepo.countByRegistrationStatus("PENDING");
        long approvedStartups = startupRepo.countByRegistrationStatus("APPROVED");
        long rejectedStartups = startupRepo.countByRegistrationStatus("REJECTED");
        return ResponseEntity.ok(Map.of("success", true, "data", Map.of(
                "pendingApprovals", pendingApprovals,
                "approvedStartups", approvedStartups,
                "rejectedStartups", rejectedStartups
        )));
    }

    // Export startups as CSV
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportStartups(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String format) {

        List<StartupReg> startups;
        if (status != null && !status.isEmpty()) {
            startups = startupRepo.findByRegistrationStatus(status);
        } else {
            startups = startupRepo.findAll();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        StringBuilder csv = new StringBuilder();
        csv.append("ID,Name,Email,Phone,Country,Address,Industry,Stage,Description,Status,Verified,Website,Created At\n");

        for (StartupReg s : startups) {
            csv.append(escapeCSV(s.getId())).append(",");
            csv.append(escapeCSV(s.getName())).append(",");
            csv.append(escapeCSV(s.getEmail())).append(",");
            csv.append(escapeCSV(s.getPhone())).append(",");
            csv.append(escapeCSV(s.getCountry())).append(",");
            csv.append(escapeCSV(s.getAddress())).append(",");
            csv.append(escapeCSV(s.getIndustry())).append(",");
            csv.append(escapeCSV(s.getStage())).append(",");
            csv.append(escapeCSV(s.getDescription())).append(",");
            csv.append(escapeCSV(s.getRegistrationStatus())).append(",");
            csv.append(s.getIsVerified() != null && s.getIsVerified() ? "Yes" : "No").append(",");
            csv.append(escapeCSV(s.getWebsite())).append(",");
            csv.append(s.getCreatedAt() != null ? dateFormat.format(s.getCreatedAt()) : "").append("\n");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "startups-export.csv");

        return new ResponseEntity<>(csv.toString().getBytes(), headers, HttpStatus.OK);
    }

    // Download a document
    @GetMapping("/documents/{docId}/download")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable String docId) {
        try {
            System.out.println("📥 Download request for document ID: " + docId);

            Optional<DocumentFile> docOpt = documentFileRepository.findById(docId);
            if (docOpt.isEmpty()) {
                System.err.println("❌ Document not found: " + docId);
                return ResponseEntity.status(404).build();
            }

            DocumentFile doc = docOpt.get();
            System.out.println("📄 Found document: " + doc.getFilePath());

            File file = new File(doc.getFilePath());
            if (!file.exists()) {
                System.err.println("❌ File not found on disk: " + doc.getFilePath());
                return ResponseEntity.status(404).build();
            }

            // Read file bytes
            byte[] fileContent = Files.readAllBytes(file.toPath());
            System.out.println("✅ File read successfully: " + fileContent.length + " bytes");

            // Determine content type
            String contentType = "application/octet-stream";
            String fileName = file.getName().toLowerCase();
            if (fileName.endsWith(".pdf")) {
                contentType = "application/pdf";
            } else if (fileName.endsWith(".doc")) {
                contentType = "application/msword";
            } else if (fileName.endsWith(".docx")) {
                contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (fileName.endsWith(".png")) {
                contentType = "image/png";
            } else if (fileName.endsWith(".txt")) {
                contentType = "text/plain";
            } else if (fileName.endsWith(".csv")) {
                contentType = "text/csv";
            } else if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentDispositionFormData("attachment", file.getName());
            headers.setContentLength(fileContent.length);

            System.out.println("✅ Sending file: " + file.getName() + " (" + contentType + ")");
            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("❌ Error downloading document: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    // Helper method to escape CSV values
    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    // Test endpoint for debugging
    @GetMapping("/test-docs/{startupId}")
    public ResponseEntity<?> testDocuments(@PathVariable String startupId) {
        System.out.println("========== TESTING DOCUMENT QUERY ==========");
        System.out.println("StartupID: " + startupId);

        try {
            // Test 1: Repository
            List<DocumentFile> docs1 = documentFileRepository.findByStartupId(startupId);
            System.out.println("✅ Repository found: " + docs1.size());

            // Test 2: MongoTemplate
            Query query = new Query(Criteria.where("startupId").is(startupId));
            List<DocumentFile> docs2 = mongoTemplate.find(query, DocumentFile.class, "documents");
            System.out.println("✅ MongoTemplate found: " + docs2.size());

            // Test 3: Count all documents
            long total = mongoTemplate.count(new Query(), "documents");
            System.out.println("✅ Total docs in collection: " + total);

            // Test 4: Get all and filter manually
            List<DocumentFile> allDocs = mongoTemplate.findAll(DocumentFile.class, "documents");
            long matching = allDocs.stream()
                    .filter(d -> startupId.equals(d.getStartupId()))
                    .count();
            System.out.println("✅ Manual filter found: " + matching);

            System.out.println("========== TEST COMPLETE ==========");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "repositoryCount", docs1.size(),
                    "mongoTemplateCount", docs2.size(),
                    "totalInCollection", total,
                    "manualFilterCount", matching,
                    "repositoryDocs", docs1,
                    "mongoTemplateDocs", docs2
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}