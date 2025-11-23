package com.launchpad.controller;

import com.launchpad.dto.StartupProfileDTO;
import com.launchpad.services.StartupProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/startups")
@CrossOrigin(origins = "*")
public class StartupProfileController {

    @Autowired
    private StartupProfileService startupProfileService;

    @GetMapping("/{id}")
    public ResponseEntity<StartupProfileDTO> getStartupById(@PathVariable String id) {
        try {
            StartupProfileDTO profile = startupProfileService.getStartupProfile(id);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<StartupProfileDTO> getStartupByEmail(@PathVariable String email) {
        try {
            StartupProfileDTO profile = startupProfileService.getStartupProfileByEmail(email);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<StartupProfileDTO> updateStartupProfile(
            @PathVariable String id,
            @RequestBody StartupProfileDTO dto) {
        try {
            StartupProfileDTO updated = startupProfileService.updateStartupProfile(id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}