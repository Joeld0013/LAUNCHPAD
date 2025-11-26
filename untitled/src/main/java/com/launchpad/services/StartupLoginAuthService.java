package com.launchpad.services;

import com.launchpad.dto.StartupLoginRequest;
import com.launchpad.dto.StartupLoginResponse;
import com.launchpad.model.Startup;
import com.launchpad.repository.StartupProfileRepository;
import com.launchpad.shared.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StartupLoginAuthService {

    private static final Logger logger = LoggerFactory.getLogger(StartupLoginAuthService.class);

    @Autowired
    private StartupProfileRepository startupRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Renamed from 'login' to 'authenticateStartup' to match your Controller calls
    public StartupLoginResponse authenticateStartup(StartupLoginRequest request) {
        logger.info("═══════════════════════════════════");
        logger.info("STARTUP LOGIN ATTEMPT");
        logger.info("═══════════════════════════════════");
        logger.info("Email: {}", request.getEmail());

        Optional<Startup> startupOpt = startupRepository.findByEmail(request.getEmail());

        if (startupOpt.isEmpty()) {
            logger.warn("✗ Startup not found with email: {}", request.getEmail());
            throw new RuntimeException("Invalid credentials");
        }

        Startup startup = startupOpt.get();
        logger.info("✓ Startup found: {}", startup.getName());
        logger.info("  → ID: {}", startup.getId());
        logger.info("  → Registration Status: {}", startup.getRegistrationStatus());

        if (!"APPROVED".equalsIgnoreCase(startup.getRegistrationStatus())) {
            logger.warn("✗ Startup not approved. Status: {}", startup.getRegistrationStatus());
            throw new RuntimeException("Account not approved yet");
        }

        if (!passwordEncoder.matches(request.getPassword(), startup.getPasswordHash())) {
            logger.warn("✗ Invalid password");
            throw new RuntimeException("Invalid credentials");
        }

        logger.info("✓ Password verified");

        // ═══════════════════════════════════════════════════════════════
        // CRITICAL: ONLY CALL generateToken ONCE with CORRECT PARAMETERS
        // Method signature: generateToken(userId, email, userType)
        // ═══════════════════════════════════════════════════════════════

        String token = jwtUtil.generateToken(
                startup.getId(),       // 1st parameter: userId (the MongoDB _id)
                startup.getEmail(),    // 2nd parameter: email
                "STARTUP"              // 3rd parameter: userType (MUST be "STARTUP")
        );

        logger.info("═══════════════════════════════════");
        logger.info("✓ LOGIN SUCCESSFUL");
        logger.info("═══════════════════════════════════");
        logger.info("  → Token generated for ID: {}", startup.getId());

        return new StartupLoginResponse(
                token,
                startup.getId(),
                startup.getName(),
                startup.getEmail(),
                "STARTUP"
        );
    }

    // Preserved Helper Methods from Original File (Restored)
    public Startup getStartupById(String id) {
        return startupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Startup not found"));
    }

    public Startup getStartupByEmail(String email) {
        return startupRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Startup not found"));
    }
}