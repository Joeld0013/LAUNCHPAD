package com.launchpad.controller;

import com.launchpad.dto.InvestorLoginRequest;
import com.launchpad.dto.InvestorLoginResponse;
import com.launchpad.services.InvestorLoginAuthService;
import com.launchpad.shared.utils.JwtUtil;
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

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody InvestorLoginRequest loginRequest) {
        try {
            // FIX: Changed type from 'Investor' to 'InvestorLoginResponse' to match Service return type
            InvestorLoginResponse authResponse = investorAuthService.authenticateInvestor(loginRequest);

            if (authResponse == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Invalid credentials or investor not found"));
            }

            // Generate JWT token using data from the authResponse DTO
            String token = jwtUtil.generateToken(
                    authResponse.getId(),          // userId
                    authResponse.getEmail(),       // email
                    "INVESTOR"                     // userType
            );


            // Set the generated token into the response object
            authResponse.setToken(token);

            // Return the updated response object
            return ResponseEntity.ok(authResponse);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An unexpected error occurred"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            if (token == null || token.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Authorization header missing"));
            }

            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            boolean valid = jwtUtil.validateToken(token);
            if (!valid) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Invalid token"));
            }

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

        public String getMessage() { return message; }
        public long getTimestamp() { return timestamp; }
    }

    static class MessageResponse {
        private String message;
        public MessageResponse(String message) { this.message = message; }
        public String getMessage() { return message; }
    }
}