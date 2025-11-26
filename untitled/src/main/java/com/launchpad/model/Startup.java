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

    // --- MANUAL GETTERS & SETTERS (Outer Class) ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public String getStage() { return stage; }
    public void setStage(String stage) { this.stage = stage; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRegistrationStatus() { return registrationStatus; }
    public void setRegistrationStatus(String registrationStatus) { this.registrationStatus = registrationStatus; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getAbout() { return about; }
    public void setAbout(String about) { this.about = about; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getGrowthMetrics() { return growthMetrics; }
    public void setGrowthMetrics(String growthMetrics) { this.growthMetrics = growthMetrics; }

    public String getPitchVideoUrl() { return pitchVideoUrl; }
    public void setPitchVideoUrl(String pitchVideoUrl) { this.pitchVideoUrl = pitchVideoUrl; }

    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    public List<TeamMember> getTeam() { return team; }
    public void setTeam(List<TeamMember> team) { this.team = team; }

    public List<Milestone> getMilestones() { return milestones; }
    public void setMilestones(List<Milestone> milestones) { this.milestones = milestones; }

    // Nested Classes with MANUAL CONSTRUCTORS and GETTERS/SETTERS
    // REMOVED LOMBOK ANNOTATIONS HERE to avoid "constructor already defined" error
    public static class TeamMember {
        private String initials;
        private String name;
        private String title;
        private String description;

        // Manual Constructors
        public TeamMember() {}

        public TeamMember(String initials, String name, String title, String description) {
            this.initials = initials;
            this.name = name;
            this.title = title;
            this.description = description;
        }

        // Manual Getters/Setters
        public String getInitials() { return initials; }
        public void setInitials(String initials) { this.initials = initials; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    // REMOVED LOMBOK ANNOTATIONS HERE to avoid "constructor already defined" error
    public static class Milestone {
        private String icon;
        private String title;
        private String date;

        // Manual Constructors
        public Milestone() {}

        public Milestone(String icon, String title, String date) {
            this.icon = icon;
            this.title = title;
            this.date = date;
        }

        // Manual Getters/Setters
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
    }
}