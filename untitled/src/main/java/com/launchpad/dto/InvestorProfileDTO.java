package com.launchpad.dto;

import java.util.List;

public class InvestorProfileDTO {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String country;
    private String organization;
    private String address;
    private String investorType;
    private String title;
    private String location;
    private String about;
    private String website;
    private String profilePicture;
    private InvestmentThesisDTO investmentThesis;
    private List<String> investmentFocus;
    private List<TeamMemberDTO> team;
    private List<PortfolioCompanyDTO> portfolio;

    // Constructors
    public InvestorProfileDTO() {}

    // Nested DTOs
    public static class InvestmentThesisDTO {
        private String stage;
        private String checkSize;
        private String horizon;

        public InvestmentThesisDTO() {}

        public InvestmentThesisDTO(String stage, String checkSize, String horizon) {
            this.stage = stage;
            this.checkSize = checkSize;
            this.horizon = horizon;
        }

        public String getStage() { return stage; }
        public void setStage(String stage) { this.stage = stage; }
        public String getCheckSize() { return checkSize; }
        public void setCheckSize(String checkSize) { this.checkSize = checkSize; }
        public String getHorizon() { return horizon; }
        public void setHorizon(String horizon) { this.horizon = horizon; }
    }

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

    public static class PortfolioCompanyDTO {
        private String initials;
        private String name;
        private String sector;
        private String description;

        public PortfolioCompanyDTO() {}

        public PortfolioCompanyDTO(String initials, String name, String sector, String description) {
            this.initials = initials;
            this.name = name;
            this.sector = sector;
            this.description = description;
        }

        public String getInitials() { return initials; }
        public void setInitials(String initials) { this.initials = initials; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSector() { return sector; }
        public void setSector(String sector) { this.sector = sector; }
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

    // Main class getters and setters
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

    public InvestmentThesisDTO getInvestmentThesis() { return investmentThesis; }
    public void setInvestmentThesis(InvestmentThesisDTO investmentThesis) { this.investmentThesis = investmentThesis; }

    public List<String> getInvestmentFocus() { return investmentFocus; }
    public void setInvestmentFocus(List<String> investmentFocus) { this.investmentFocus = investmentFocus; }

    public List<TeamMemberDTO> getTeam() { return team; }
    public void setTeam(List<TeamMemberDTO> team) { this.team = team; }

    public List<PortfolioCompanyDTO> getPortfolio() { return portfolio; }
    public void setPortfolio(List<PortfolioCompanyDTO> portfolio) { this.portfolio = portfolio; }
}