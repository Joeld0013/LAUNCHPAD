package com.launchpad.admin.dto;

import com.launchpad.registration.model.RegistrationStatus;  // Use registration enum

public class StartupFilterDTO {

    private String industry;
    private String stage;
    private String country;
    private RegistrationStatus registrationStatus;
    private String searchTerm;

    public StartupFilterDTO() {}

    // Getters and Setters
    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public String getStage() { return stage; }
    public void setStage(String stage) { this.stage = stage; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public RegistrationStatus getRegistrationStatus() { return registrationStatus; }
    public void setRegistrationStatus(RegistrationStatus registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public String getSearchTerm() { return searchTerm; }
    public void setSearchTerm(String searchTerm) { this.searchTerm = searchTerm; }
}