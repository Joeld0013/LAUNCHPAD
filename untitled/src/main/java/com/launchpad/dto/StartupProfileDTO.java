package com.launchpad.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.ArrayList;

@Data
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

    // --- Inner Classes with MANUAL Getters/Setters to ensure Jackson works ---

    public static class TeamMemberDTO {
        private String initials;
        private String name;
        private String title;
        private String description;

        public TeamMemberDTO() {} // Needed for JSON

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

        public MilestoneDTO() {} // Needed for JSON

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