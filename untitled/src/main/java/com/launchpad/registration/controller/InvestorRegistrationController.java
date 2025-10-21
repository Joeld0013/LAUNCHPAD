package com.launchpad.registration.controller;

import com.launchpad.registration.dto.InvestorRegistrationRequest;
import com.launchpad.registration.model.InvestorReg;
import com.launchpad.registration.services.InvestorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/investor")
@CrossOrigin(origins = "*") // Allow CORS for frontend
public class InvestorRegistrationController {

    @Autowired
    private InvestorService investorService;

    /**
     * Accepts multipart/form-data fields and files for investor registration.
     */
    @PostMapping(path = "/register", consumes = {"multipart/form-data"})
    public ResponseEntity<?> registerInvestor(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String password,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String organization,
            @RequestParam(required = false) String address,
            @RequestParam String investorType, // angel, vc, institutional, other
            @RequestParam(required = false) String preferences,
            @RequestParam(required = false) String docType,
            @RequestParam(value = "documents", required = false) List<MultipartFile> documents
    ) {
        try {
            InvestorRegistrationRequest req = new InvestorRegistrationRequest();
            req.setName(name);
            req.setEmail(email);
            req.setPhone(phone);
            req.setPassword(password);
            req.setCountry(country);
            req.setOrganization(organization);
            req.setAddress(address);
            req.setInvestorType(investorType);
            req.setPreferences(preferences);
            req.setDocType(docType);

            InvestorReg saved = investorService.registerInvestor(req, documents);
            return ResponseEntity.ok("Registration successful! Pending admin approval. Investor ID: " + saved.getId());
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
    public ResponseEntity<?> approve(@PathVariable("id") String investorId) {
        try {
            InvestorReg investor = investorService.approveInvestor(investorId);
            return ResponseEntity.ok("Investor approved and credentials emailed to " + investor.getEmail());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error: " + ex.getMessage());
        }
    }

    // ADMIN: Reject registration
    @PostMapping("/admin/{id}/reject")
    public ResponseEntity<?> reject(@PathVariable("id") String investorId, @RequestParam(required = false) String reason) {
        try {
            InvestorReg investor = investorService.rejectInvestor(investorId, reason);
            return ResponseEntity.ok("Investor rejected: " + investor.getId());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error: " + ex.getMessage());
        }
    }

    // Get all registrations for admin
    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllRegistrations() {
        try {
            List<InvestorReg> investors = investorService.getAllInvestors();
            return ResponseEntity.ok(investors);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error: " + ex.getMessage());
        }
    }
}