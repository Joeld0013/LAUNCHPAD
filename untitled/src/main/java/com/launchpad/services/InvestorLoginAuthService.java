package com.launchpad.services;

import com.launchpad.dto.InvestorLoginRequest;
import com.launchpad.dto.InvestorLoginResponse;
import com.launchpad.model.InvestorLogin;
import com.launchpad.repository.InvestorLoginRepository;
import com.launchpad.shared.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class InvestorLoginAuthService {

    @Autowired
    private InvestorLoginRepository investorLoginRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public InvestorLoginResponse authenticateInvestor(InvestorLoginRequest loginRequest) {
        // Find investor by email
        InvestorLogin investor = investorLoginRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Check if password matches
        if (!passwordEncoder.matches(loginRequest.getPassword(), investor.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Check if registration is approved
        if (investor.getRegistrationStatus() != InvestorLogin.RegistrationStatus.APPROVED) {
            String statusMessage;
            switch (investor.getRegistrationStatus()) {
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

        String token = jwtUtil.generateToken(investor.getId(), investor.getEmail(), "INVESTOR", expirationTime);

        // Create and return response
        return new InvestorLoginResponse(
                token,
                investor.getId(),
                investor.getEmail(),
                investor.getName(),
                "Login successful"
        );
    }

    public InvestorLogin getInvestorById(String id) {
        return investorLoginRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Investor not found"));
    }

    public InvestorLogin getInvestorByEmail(String email) {
        return investorLoginRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Investor not found"));
    }
}