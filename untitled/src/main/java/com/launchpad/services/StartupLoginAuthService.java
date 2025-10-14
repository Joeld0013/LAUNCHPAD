package com.launchpad.services;

import com.launchpad.dto.StartupLoginRequest;
import com.launchpad.dto.StartupLoginResponse;
import com.launchpad.model.StartupLogin;
import com.launchpad.repository.StartupLoginRepository;
import com.launchpad.shared.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class StartupLoginAuthService {

    @Autowired
    private StartupLoginRepository startupLoginRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public StartupLoginResponse authenticateStartup(StartupLoginRequest loginRequest) {
        // Find startup by email
        StartupLogin startup = startupLoginRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Check if password matches
        if (!passwordEncoder.matches(loginRequest.getPassword(), startup.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Check if registration is approved
        if (startup.getRegistrationStatus() != StartupLogin.RegistrationStatus.APPROVED) {
            String statusMessage;
            switch (startup.getRegistrationStatus()) {
                case PENDING:
                    statusMessage = "Your registration is pending admin approval. Please wait for verification.";
                    break;
                case REJECTED:
                    statusMessage = "Your registration has been rejected. Please contact support for more information.";
                    break;
                default:
                    statusMessage = "Your account is not approved for login.";
            }
            throw new RuntimeException(statusMessage);
        }

        // Generate JWT token
        long expirationTime = loginRequest.getRememberMe() != null && loginRequest.getRememberMe()
                ? 30 * 24 * 60 * 60 * 1000L  // 30 days
                : 24 * 60 * 60 * 1000L;       // 24 hours

        String token = jwtUtil.generateToken(startup.getId(), startup.getEmail(), "STARTUP", expirationTime);

        // Create and return response
        return new StartupLoginResponse(
                token,
                startup.getId(),
                startup.getEmail(),
                startup.getName(),
                "Login successful"
        );
    }

    public StartupLogin getStartupById(String id) {
        return startupLoginRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Startup not found"));
    }

    public StartupLogin getStartupByEmail(String email) {
        return startupLoginRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Startup not found"));
    }
}