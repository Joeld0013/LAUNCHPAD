// com/launchpad/controller/AdminController.java
package com.launchpad.admin.controller;

import com.launchpad.admin.dto.AdminLoginRequest;
import com.launchpad.admin.dto.AdminLoginResponse;
import com.launchpad.admin.services.AdminLoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:8080", "http://127.0.0.1:8080", "http://localhost:3000"},
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class AdminLoginController {

    private static final Logger logger = LoggerFactory.getLogger(AdminLoginController.class);

    @Autowired
    private AdminLoginService adminService;

    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponse> login(@Valid @RequestBody AdminLoginRequest loginRequest) {
        logger.info("Admin login attempt: {}", loginRequest.getEmail());

        try {
            AdminLoginResponse response = adminService.authenticateAdmin(loginRequest);

            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            logger.error("Error during admin login: {}", e.getMessage(), e);
            AdminLoginResponse errorResponse = new AdminLoginResponse("Internal server error", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<String> createAdmin(@RequestParam String email, @RequestParam String password) {
        logger.info("Creating admin: {}", email);

        try {
            boolean created = adminService.createAdmin(email, password);

            if (created) {
                return ResponseEntity.ok("Admin created successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to create admin");
            }
        } catch (Exception e) {
            logger.error("Error creating admin: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        logger.info("Admin test endpoint called");
        return ResponseEntity.ok("Admin controller is working!");
    }

    @GetMapping("/debug")
    public ResponseEntity<String> debug() {
        logger.info("Debug endpoint called");
        try {
            long adminCount = adminService.getAdminCount();
            return ResponseEntity.ok("Debug: Found " + adminCount + " admins in database");
        } catch (Exception e) {
            return ResponseEntity.ok("Debug error: " + e.getMessage());
        }
    }

    @RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> handleOptions() {
        return ResponseEntity.ok().build();
    }
}