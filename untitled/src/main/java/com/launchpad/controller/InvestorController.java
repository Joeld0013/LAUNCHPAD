package com.launchpad.controller;

import com.launchpad.dto.InvestorProfileDTO;
import com.launchpad.services.InvestorProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/investors")
@CrossOrigin(origins = "*")
public class InvestorController {

    @Autowired
    private InvestorProfileService investorProfileService;

    @GetMapping("/{id}")
    public ResponseEntity<InvestorProfileDTO> getInvestorById(@PathVariable String id) {
        try {
            InvestorProfileDTO profile = investorProfileService.getInvestorProfile(id);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<InvestorProfileDTO> getInvestorByEmail(@PathVariable String email) {
        try {
            InvestorProfileDTO profile = investorProfileService.getInvestorProfileByEmail(email);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvestorProfileDTO> updateInvestorProfile(
            @PathVariable String id,
            @RequestBody InvestorProfileDTO dto) {
        try {
            InvestorProfileDTO updated = investorProfileService.updateInvestorProfile(id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}