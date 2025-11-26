package com.launchpad.services;

import com.launchpad.dto.InvestorLoginRequest;
import com.launchpad.dto.InvestorLoginResponse;
import com.launchpad.model.Investor;
import com.launchpad.repository.InvestorRepository;
import com.launchpad.shared.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InvestorLoginAuthService {

    private static final Logger logger = LoggerFactory.getLogger(InvestorLoginAuthService.class);

    @Autowired
    private InvestorRepository investorRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Renamed from 'login' to 'authenticateInvestor' to match your Controller calls
    public InvestorLoginResponse authenticateInvestor(InvestorLoginRequest request) {
        logger.info("═══════════════════════════════════");
        logger.info("INVESTOR LOGIN ATTEMPT");
        logger.info("═══════════════════════════════════");
        logger.info("Email: {}", request.getEmail());

        Optional<Investor> investorOpt = investorRepository.findByEmail(request.getEmail());

        if (investorOpt.isEmpty()) {
            logger.warn("✗ Investor not found with email: {}", request.getEmail());
            throw new RuntimeException("Invalid credentials");
        }

        Investor investor = investorOpt.get();
        logger.info("✓ Investor found: {}", investor.getName());
        logger.info("  → ID: {}", investor.getId());
        logger.info("  → Registration Status: {}", investor.getRegistrationStatus());

        if (!"APPROVED".equalsIgnoreCase(investor.getRegistrationStatus())) {
            logger.warn("✗ Investor not approved. Status: {}", investor.getRegistrationStatus());
            throw new RuntimeException("Account not approved yet");
        }

        if (!passwordEncoder.matches(request.getPassword(), investor.getPasswordHash())) {
            logger.warn("✗ Invalid password");
            throw new RuntimeException("Invalid credentials");
        }

        logger.info("✓ Password verified");

        // ═══════════════════════════════════════════════════════════════
        // CRITICAL: ONLY CALL generateToken ONCE with CORRECT PARAMETERS
        // Method signature: generateToken(userId, email, userType)
        // ═══════════════════════════════════════════════════════════════

        String token = jwtUtil.generateToken(
                investor.getId(),      // 1st parameter: userId (the MongoDB _id)
                investor.getEmail(),   // 2nd parameter: email
                "INVESTOR"             // 3rd parameter: userType (MUST be "INVESTOR")
        );

        logger.info("═══════════════════════════════════");
        logger.info("✓ LOGIN SUCCESSFUL");
        logger.info("═══════════════════════════════════");
        logger.info("  → Token generated for ID: {}", investor.getId());

        return new InvestorLoginResponse(
                token,
                investor.getId(),
                investor.getName(),
                investor.getEmail(),
                "INVESTOR"
        );
    }

    // Preserved Helper Methods from Original File (Restored)
    public Investor getInvestorById(String id) {
        return investorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Investor not found"));
    }

    public Investor getInvestorByEmail(String email) {
        return investorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Investor not found"));
    }
}