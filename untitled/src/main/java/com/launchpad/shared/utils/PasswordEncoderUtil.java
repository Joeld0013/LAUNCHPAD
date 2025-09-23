package com.launchpad.shared.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderUtil {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Replace "your_password" with your actual admin password
        String rawPassword = "admin123"; // Change this to your desired password
        String encodedPassword = encoder.encode(rawPassword);

        System.out.println("Raw Password: " + rawPassword);
        System.out.println("Encoded Password: " + encodedPassword);
        System.out.println("\nUse this encoded password in your MongoDB admin document.");

        // Test verification
        boolean matches = encoder.matches(rawPassword, encodedPassword);
        System.out.println("Verification test: " + matches);
    }
}