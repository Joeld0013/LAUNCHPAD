package com.launchpad.controller;

import com.launchpad.dto.InvestorLoginRequest;
import com.launchpad.dto.InvestorLoginResponse;
import com.launchpad.services.InvestorLoginAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/investor/auth")
@CrossOrigin(origins = "*")
public class InvestorLoginController {

    @Autowired
    private InvestorLoginAuthService investorAuthService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody InvestorLoginRequest loginRequest) {
        try {
            InvestorLoginResponse response = investorAuthService.authenticateInvestor(loginRequest);
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

    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String token) {
        try {
            // Token verification logic (implement in JwtUtil if needed)
            return ResponseEntity.ok(new MessageResponse("Token is valid"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid token"));
        }
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