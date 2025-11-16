package com.launchpad.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String password; // Hashed password

    @Indexed(unique = true)
    private String username;

    private String companyName;
    private String title; // CEO, Investor, etc.
    private String location;
    private String about;

    private UserType userType; // STARTUP or INVESTOR

    // Contact Information
    private String website;
    private String phone;

    // Profile avatar (initials)
    private String avatar;

    private boolean isVerified = false;
    private boolean isActive = true;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum UserType {
        STARTUP,
        INVESTOR
    }
}