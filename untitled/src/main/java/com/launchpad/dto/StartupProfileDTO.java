package com.launchpad.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.ArrayList;

@Data // Kept for other features like toString(), but manual methods take priority
@AllArgsConstructor
@NoArgsConstructor
public class StartupProfileDTO {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String country;
    private String address;
    private String industry;
    private String stage;
    private String description;

    private String title;
    private String location;
    private String about;

    private String website;
    private String growthMetrics;
    private String pitchVideoUrl;
    private String profilePicture;

    private List<String> skills = new ArrayList<>();

    // IMPORTANT: Ensure these match the JSON keys exactly
    private List<TeamMemberDTO> team = new ArrayList<>();
    private List<MilestoneDTO> milestones = new ArrayList<>();

    // --- MANUAL GETTERS & SETTERS (Fixes "Cannot resolve method" errors) ---

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

    public List<TeamMemberDTO> getTeam() { return team; }
    public void setTeam(List<TeamMemberDTO> team) { this.team = team; }

    public List<MilestoneDTO> getMilestones() { return milestones; }
    public void setMilestones(List<MilestoneDTO> milestones) { this.milestones = milestones; }

    // --- Inner Classes with MANUAL Getters/Setters ---

    public static class TeamMemberDTO {
        private String initials;
        private String name;
        private String title;
        private String description;

        public TeamMemberDTO() {}

        public TeamMemberDTO(String initials, String name, String title, String description) {
            this.initials = initials;
            this.name = name;
            this.title = title;
            this.description = description;
        }

        public String getInitials() { return initials; }
        public void setInitials(String initials) { this.initials = initials; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class MilestoneDTO {
        private String icon;
        private String title;
        private String date;

        public MilestoneDTO() {}

        public MilestoneDTO(String icon, String title, String date) {
            this.icon = icon;
            this.title = title;
            this.date = date;
        }

        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
    }
}