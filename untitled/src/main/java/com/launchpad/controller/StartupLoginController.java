package com.launchpad.controller;

import com.launchpad.dto.StartupLoginRequest;
import com.launchpad.dto.StartupLoginResponse;
import com.launchpad.services.StartupLoginAuthService;
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody StartupLoginRequest loginRequest) {
        try {

            StartupLoginResponse response = startupAuthService.authenticateStartup(loginRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        // Implement token blacklisting if needed
        return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
    }

    // Helper classes
    static class ErrorResponse {
        private String message;
        private long timestamp;

        public ErrorResponse(String message) {
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        public String getMessage() {
            return message;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }

    static class MessageResponse {
        private String message;

        public MessageResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}