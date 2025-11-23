package com.launchpad.admin.dto;

import com.launchpad.admin.model.PastInvestment;
import java.time.LocalDateTime;
import java.util.List;

public class InvestorDetailDTO {
    private String _id;
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
    private LocalDateTime createdAt;
    private String investmentRange;
    private List<PastInvestment> pastInvestments;
    private List<InvestorDocumentDTO> documents;
    private String rejectionReason;

    public InvestorDetailDTO() {}

    // Getters and Setters (full implementation)
    public String get_id() { return _id; }
    public void set_id(String _id) { this._id = _id; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getInvestmentRange() { return investmentRange; }
    public void setInvestmentRange(String investmentRange) { this.investmentRange = investmentRange; }

    public List<PastInvestment> getPastInvestments() { return pastInvestments; }
    public void setPastInvestments(List<PastInvestment> pastInvestments) { this.pastInvestments = pastInvestments; }

    public List<InvestorDocumentDTO> getDocuments() { return documents; }
    public void setDocuments(List<InvestorDocumentDTO> documents) { this.documents = documents; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}