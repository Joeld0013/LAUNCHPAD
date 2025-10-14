package com.launchpad.admin.controller;

import com.launchpad.registration.model.Startup;
import com.launchpad.registration.repository.StartupRepository;
import com.launchpad.registration.services.EmailService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.util.*;
import java.text.SimpleDateFormat;

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

    // UPDATED: Now supports search parameter
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

            List<Startup> startups = mongoTemplate.find(query, Startup.class);

            System.out.println("Search query: " + search);
            System.out.println("Found startups: " + startups.size());

            return ResponseEntity.ok(Map.of("success", true, "data", startups));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStartupDetail(@PathVariable String id) {
        Optional<Startup> startupOpt = startupRepo.findById(id);
        if (startupOpt.isEmpty())
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Startup not found"));
        Startup s = startupOpt.get();
        return ResponseEntity.ok(Map.of("success", true, "data", s));
    }

    @PostMapping("/approve")
    public ResponseEntity<?> approveStartup(@RequestBody Map<String, Object> body) {
        String startupId = (String) body.get("startupId");
        Optional<Startup> startupOpt = startupRepo.findById(startupId);
        if (startupOpt.isEmpty())
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Startup not found"));
        Startup s = startupOpt.get();
        s.setRegistrationStatus("APPROVED");
        s.setIsVerified(true);
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

    @PostMapping("/reject")
    public ResponseEntity<?> rejectStartup(@RequestBody Map<String, Object> body) {
        String startupId = (String) body.get("startupId");
        String comments = (String) body.getOrDefault("comments", "Rejected by admin.");
        Optional<Startup> startupOpt = startupRepo.findById(startupId);
        if (startupOpt.isEmpty())
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Startup not found"));
        Startup s = startupOpt.get();
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

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportStartups(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String format) {

        List<Startup> startups;
        if (status != null && !status.isEmpty()) {
            startups = startupRepo.findByRegistrationStatus(status);
        } else {
            startups = startupRepo.findAll();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        StringBuilder csv = new StringBuilder();
        csv.append("ID,Name,Email,Phone,Country,Address,Industry,Stage,Description,Status,Verified,Website,Created At\n");

        for (Startup s : startups) {
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
            csv.append(s.isVerified() ? "Yes" : "No").append(",");
            csv.append(escapeCSV(s.getWebsite())).append(",");
            csv.append(s.getCreatedAt() != null ? dateFormat.format(java.sql.Timestamp.valueOf(s.getCreatedAt())) : "").append("\n");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "startups-export.csv");

        return new ResponseEntity<>(csv.toString().getBytes(), headers, HttpStatus.OK);
    }

    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    @GetMapping("/documents/{docId}/download")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable String docId) {
        return ResponseEntity.ok(new byte[0]);
    }
}