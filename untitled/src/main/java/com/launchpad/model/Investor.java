package com.launchpad.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;

@Document(collection = "investors")
public class Investor {
    @Id
    private String id;

    // Basic Info
    private String name;
    private String email;
    private String phone;
    private String country;
    private String organization;
    private String address;
    private String investorType;
    private String preferences;
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

    // Profile Picture
    private String profilePicture;

    // Investment Thesis
    private InvestmentThesis investmentThesis;

    // Investment Focus (sectors)
    private List<String> investmentFocus;

    // Team Members
    private List<TeamMember> team;

    // Portfolio Companies
    private List<PortfolioCompany> portfolio;

    // Milestones - REMOVED (not needed for investors)
    // private List<Milestone> milestones;

    // Nested Classes
    public static class InvestmentThesis {
        private String stage; // e.g., "Pre-Seed, Seed"
        private String checkSize; // e.g., "$250,000 - $1,000,000"
        private String horizon; // e.g., "3-5 Years"

        public String getStage() { return stage; }
        public void setStage(String stage) { this.stage = stage; }
        public String getCheckSize() { return checkSize; }
        public void setCheckSize(String checkSize) { this.checkSize = checkSize; }
        public String getHorizon() { return horizon; }
        public void setHorizon(String horizon) { this.horizon = horizon; }
    }

    public static class TeamMember {
        private String initials;
        private String name;
        private String title;
        private String description;

        public String getInitials() { return initials; }
        public void setInitials(String initials) { this.initials = initials; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class PortfolioCompany {
        private String initials;
        private String name;
        private String sector;
        private String description;

        public String getInitials() { return initials; }
        public void setInitials(String initials) { this.initials = initials; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSector() { return sector; }
        public void setSector(String sector) { this.sector = sector; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class Milestone {
        private String icon;
        private String title;
        private String date;

        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
    }

    // Getters and Setters for main class
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

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getInvestorType() { return investorType; }
    public void setInvestorType(String investorType) { this.investorType = investorType; }

    public String getPreferences() { return preferences; }
    public void setPreferences(String preferences) { this.preferences = preferences; }

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

    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    public InvestmentThesis getInvestmentThesis() { return investmentThesis; }
    public void setInvestmentThesis(InvestmentThesis investmentThesis) { this.investmentThesis = investmentThesis; }

    public List<String> getInvestmentFocus() { return investmentFocus; }
    public void setInvestmentFocus(List<String> investmentFocus) { this.investmentFocus = investmentFocus; }

    public List<TeamMember> getTeam() { return team; }
    public void setTeam(List<TeamMember> team) { this.team = team; }

    public List<PortfolioCompany> getPortfolio() { return portfolio; }
    public void setPortfolio(List<PortfolioCompany> portfolio) { this.portfolio = portfolio; }
}