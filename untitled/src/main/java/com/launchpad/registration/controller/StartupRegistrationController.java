package com.launchpad.registration.controller;

import com.launchpad.registration.dto.StartupRegistrationRequest;
import com.launchpad.registration.model.Startup;
import com.launchpad.registration.services.StartupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/startup")
@CrossOrigin(origins = "*") // Allow CORS for frontend
public class StartupRegistrationController {

    @Autowired
    private StartupService startupService;

    /**
     * Accepts multipart/form-data fields and files.
     * Updated to include password and website fields.
     */
    @PostMapping(path = "/register", consumes = {"multipart/form-data"})
    public ResponseEntity<?> registerStartup(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String password, // ✅ Add password parameter
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String stage,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String website, // ✅ Add website parameter
            @RequestParam(required = false) String docType, // ✅ Add docType parameter
            @RequestParam(value = "documents", required = false) List<MultipartFile> documents
    ) {
        try {
            StartupRegistrationRequest req = new StartupRegistrationRequest();
            req.setName(name);
            req.setEmail(email);
            req.setPhone(phone);
            req.setPassword(password); // ✅ Set password
            req.setCountry(country);
            req.setAddress(address);
            req.setIndustry(industry);
            req.setStage(stage);
            req.setDescription(description);
            req.setWebsite(website); // ✅ Set website
            req.setDocType(docType); // ✅ Set docType

            Startup saved = startupService.registerStartup(req, documents);
            return ResponseEntity.ok("Registration successful! Pending admin approval. Startup ID: " + saved.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Registration error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    // ADMIN: Approve registration
    @PostMapping("/admin/{id}/approve")
    public ResponseEntity<?> approve(@PathVariable("id") String startupId) {
        try {
            Startup s = startupService.approveStartup(startupId);
            return ResponseEntity.ok("Startup approved and credentials emailed to " + s.getEmail());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error: " + ex.getMessage());
        }
    }

    // ADMIN: Reject registration
    @PostMapping("/admin/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable("id") String startupId, @RequestParam(required=false) String reason) {
        try {
            Startup s = startupService.rejectStartup(startupId, reason);
            return ResponseEntity.ok("Startup rejected: " + s.getId());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error: " + ex.getMessage());
        }
    }

    // ✅ Add endpoint to get all registrations for admin
    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllRegistrations() {
        try {
            List<Startup> startups = startupService.getAllStartups();
            return ResponseEntity.ok(startups);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error: " + ex.getMessage());
        }
    }
}