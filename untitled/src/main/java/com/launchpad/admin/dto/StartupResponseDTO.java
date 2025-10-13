package com.launchpad.admin.dto;

import java.util.Date;
import java.util.List;

public class StartupResponseDTO {

    private String id;
    private String name;
    private String email;
    private String phone;
    private String country;
    private String address;
    private String industry;
    private String stage;
    private String description;
    private String registrationStatus;  // Changed from enum to String
    private Boolean isVerified;
    private Date createdAt;  // Changed from LocalDateTime to Date
    private Date approvedAt;
    private String website;
    private String businessPlan;
    private String foundedDate;
    private Integer teamSize;
    private String contactPerson;
    private List<DocumentDTO> documents;

    // All getters and setters remain the same
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
    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getApprovedAt() { return approvedAt; }
    public void setApprovedAt(Date approvedAt) { this.approvedAt = approvedAt; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public String getBusinessPlan() { return businessPlan; }
    public void setBusinessPlan(String businessPlan) { this.businessPlan = businessPlan; }
    public String getFoundedDate() { return foundedDate; }
    public void setFoundedDate(String foundedDate) { this.foundedDate = foundedDate; }
    public Integer getTeamSize() { return teamSize; }
    public void setTeamSize(Integer teamSize) { this.teamSize = teamSize; }
    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }
    public List<DocumentDTO> getDocuments() { return documents; }
    public void setDocuments(List<DocumentDTO> documents) { this.documents = documents; }
}