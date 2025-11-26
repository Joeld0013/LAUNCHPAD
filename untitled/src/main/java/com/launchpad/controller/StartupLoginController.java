package com.launchpad.controller;

import com.launchpad.dto.StartupLoginRequest;
import com.launchpad.dto.StartupLoginResponse;
import com.launchpad.services.StartupLoginAuthService;
import com.launchpad.shared.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/startup/auth")
@CrossOrigin(origins = "*")
public class StartupLoginController {

    @Autowired
    private StartupLoginAuthService startupAuthService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody StartupLoginRequest loginRequest) {
        try {
            // FIX: Changed type from 'Startup' to 'StartupLoginResponse' to match Service return type
            StartupLoginResponse authResponse = startupAuthService.authenticateStartup(loginRequest);

            if (authResponse == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Invalid credentials or startup not found"));
            }

            // Generate JWT token using data from the authResponse DTO
            String token = jwtUtil.generateToken(
                    authResponse.getId(),       // userId from DTO
                    "STARTUP",                  // userType
                    authResponse.getEmail()     // email from DTO
            );

            // Set the token
            authResponse.setToken(token);

            // Return the updated response
            return ResponseEntity.ok(authResponse);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
    }

    // Helper response classes
    static class ErrorResponse {
        private String message;
        private long timestamp;

        public ErrorResponse(String message) {
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }
        public String getMessage() { return message; }
        public long getTimestamp() { return timestamp; }
    }

    static class MessageResponse {
        private String message;
        public MessageResponse(String message) { this.message = message; }
        public String getMessage() { return message; }
    }
}