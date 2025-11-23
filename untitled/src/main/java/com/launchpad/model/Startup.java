package com.launchpad.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "startups")
public class Startup {
    @Id
    private String id;

    // Basic Info
    private String name;
    private String email;
    private String phone;
    private String country;
    private String address;
    private String industry;
    private String stage;
    private String description;
    private String registrationStatus;
    private boolean isVerified;
    private String passwordHash;
    private Date createdAt;

    // Profile Details
    private String title; // tagline
    private String location;
    private String about;

    // Contact Info
    private String website;

    // Media
    private String growthMetrics;
    private String pitchVideoUrl;
    private String profilePicture;

    // Skills/Focus Areas (Initialize to avoid nulls)
    private List<String> skills = new ArrayList<>();

    // Team Members (Initialize to avoid nulls)
    private List<TeamMember> team = new ArrayList<>();

    // Milestones (Initialize to avoid nulls)
    private List<Milestone> milestones = new ArrayList<>();

    // Nested Classes
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamMember {
        private String initials;
        private String name;
        private String title;
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Milestone {
        private String icon;
        private String title;
        private String date;
    }
}