// com/launchpad/services/AdminService.java - Debug Version
package com.launchpad.admin.services;

import com.launchpad.admin.dto.AdminLoginRequest;
import com.launchpad.admin.dto.AdminLoginResponse;
import com.launchpad.admin.model.Adminlogin;
import com.launchpad.admin.repository.AdminRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminLoginService {

    private static final Logger logger = LoggerFactory.getLogger(AdminLoginService.class);

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenService jwtTokenService;

    public AdminLoginResponse authenticateAdmin(AdminLoginRequest loginRequest) {
        try {
            logger.info("=== ADMIN AUTHENTICATION DEBUG ===");
            logger.info("Input email: '{}'", loginRequest.getEmail());
            logger.info("Input password: '{}'", loginRequest.getPassword());

            // Debug: Check all admins in database
            List<Adminlogin> allAdmins = adminRepository.findAll();
            logger.info("Total admins in database: {}", allAdmins.size());

            for (Adminlogin admin : allAdmins) {
                logger.info("DB Admin - Email: '{}', Password: '{}', Role: '{}', Active: {}",
                        admin.getEmail(), admin.getPassword(), admin.getRole(), admin.isActive());
            }

            // Find admin by email
            Optional<Adminlogin> adminOptional = adminRepository.findByEmail(loginRequest.getEmail());

            if (adminOptional.isEmpty()) {
                logger.error("❌ Admin not found with email: '{}'", loginRequest.getEmail());

                // Try to find admin with different email patterns
                logger.info("🔍 Searching for similar emails...");
                for (Adminlogin admin : allAdmins) {
                    if (admin.getEmail().toLowerCase().contains(loginRequest.getEmail().toLowerCase()) ||
                            loginRequest.getEmail().toLowerCase().contains(admin.getEmail().toLowerCase())) {
                        logger.info("📧 Similar email found: '{}'", admin.getEmail());
                    }
                }

                return new AdminLoginResponse("Admin not found", false);
            }

            Adminlogin admin = adminOptional.get();
            logger.info("✅ Admin found:");
            logger.info("   Email: '{}'", admin.getEmail());
            logger.info("   Password: '{}'", admin.getPassword());
            logger.info("   Role: '{}'", admin.getRole());
            logger.info("   Active: {}", admin.isActive());

            // Password verification
            logger.info("🔑 Password verification:");
            logger.info("   Input password: '{}'", loginRequest.getPassword());
            logger.info("   Stored password: '{}'", admin.getPassword());

            boolean passwordMatches = false;

            // Check if it's BCrypt encoded
            if (admin.getPassword().startsWith("$2a$") || admin.getPassword().startsWith("$2b$")) {
                logger.info("🔐 Using BCrypt verification...");
                passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), admin.getPassword());
                logger.info("   BCrypt result: {}", passwordMatches);
            } else {
                logger.info("📝 Using plain text verification...");
                passwordMatches = loginRequest.getPassword().equals(admin.getPassword());
                logger.info("   Plain text result: {}", passwordMatches);
                logger.info("   Exact match check: '{}' == '{}'", loginRequest.getPassword(), admin.getPassword());
            }

            if (!passwordMatches) {
                logger.error("❌ Password verification failed");
                logger.error("   Expected: '{}'", admin.getPassword());
                logger.error("   Received: '{}'", loginRequest.getPassword());
                return new AdminLoginResponse("Invalid password", false);
            }

            // Generate JWT token
            String role = admin.getRole() != null ? admin.getRole() : "ADMIN";
            logger.info("🎫 Generating JWT token with role: {}", role);

            String token = jwtTokenService.generateAdminToken(admin.getEmail(), role);
            logger.info("✅ Token generated: {}", token.substring(0, 20) + "...");

            logger.info("✅ Admin authentication successful for: {}", loginRequest.getEmail());

            return new AdminLoginResponse(
                    "Authentication successful",
                    token,
                    admin.getEmail(),
                    true
            );

        } catch (Exception e) {
            logger.error("💥 Error during admin authentication: {}", e.getMessage(), e);
            e.printStackTrace(); // Print full stack trace
            return new AdminLoginResponse("Authentication error: " + e.getMessage(), false);
        }
    }

    public boolean createAdmin(String email, String rawPassword) {
        try {
            if (adminRepository.existsByEmail(email)) {
                logger.warn("Admin already exists: {}", email);
                return false;
            }

            Adminlogin admin = new Adminlogin();
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode(rawPassword));
            admin.setRole("ADMIN");
            admin.setActive(true);

            adminRepository.save(admin);
            logger.info("Admin created successfully: {}", email);
            return true;

        } catch (Exception e) {
            logger.error("Error creating admin: {}", e.getMessage(), e);
            return false;
        }
    }

    public long getAdminCount() {
        try {
            long count = adminRepository.count();
            logger.info("Total admins in database: {}", count);
            return count;
        } catch (Exception e) {
            logger.error("Error counting admins: {}", e.getMessage(), e);
            return 0;
        }
    }

}