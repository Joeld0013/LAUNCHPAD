package com.launchpad.admin.controller;

import com.launchpad.registration.model.Startup;
import com.launchpad.registration.repository.StartupRepository;
import com.launchpad.registration.services.EmailService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import java.util.*;

@RestController
@RequestMapping("/api/admin/startups")
@CrossOrigin(origins = "*")
public class StartupAdminController {

    @Autowired
    private StartupRepository startupRepo;

    @Autowired
    private EmailService emailService;

    // GET /api/admin/startups?status=PENDING
    @GetMapping
    public ResponseEntity<?> getStartups(@RequestParam(required = false) String status) {
        List<Startup> startups;
        if (status != null) {
            startups = startupRepo.findByRegistrationStatus(status);
        } else {
            startups = startupRepo.findAll();
        }
        return ResponseEntity.ok(Map.of("success", true, "data", startups));
    }

    // GET /api/admin/startups/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getStartupDetail(@PathVariable String id) {
        Optional<Startup> startupOpt = startupRepo.findById(id);
        if (startupOpt.isEmpty())
            return ResponseEntity.status(404).body(Map.of("success", false, "message", "Startup not found"));
        Startup s = startupOpt.get();
        return ResponseEntity.ok(Map.of("success", true, "data", s));
    }

    // POST /api/admin/startups/approve
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
        emailService.sendApprovedEmail(s.getEmail(), s.getName());
        return ResponseEntity.ok(Map.of("success", true, "message", "Startup approved successfully"));
    }

    // POST /api/admin/startups/reject
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
        emailService.sendRejectedEmail(s.getEmail(), s.getName(), comments);
        return ResponseEntity.ok(Map.of("success", true, "message", "Startup rejected"));
    }

    // GET /api/admin/startups/stats
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

    // Dummy download endpoint
    @GetMapping("/documents/{docId}/download")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable String docId) {
        return ResponseEntity.ok(new byte[0]);
    }
}