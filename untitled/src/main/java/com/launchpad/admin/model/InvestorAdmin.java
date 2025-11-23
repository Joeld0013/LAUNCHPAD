package com.launchpad.admin.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "investors")
public class InvestorAdmin {
    @Id
    private String _id;
    private String name;
    private String email;
    private String phone;
    private String country;
    private String organization;
    private String address;
    private String investorType; // vc, angel, corporate, private-equity
    private String preferences;
    private String registrationStatus; // PENDING, APPROVED, REJECTED
    private boolean isVerified;
    private String passwordHash;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> documentIds;
    private String investmentRange;
    private List<PastInvestment> pastInvestments;
    private String rejectionReason;
    private LocalDateTime verificationDate;

    // Getters and Setters
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

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<String> getDocumentIds() { return documentIds; }
    public void setDocumentIds(List<String> documentIds) { this.documentIds = documentIds; }

    public String getInvestmentRange() { return investmentRange; }
    public void setInvestmentRange(String investmentRange) { this.investmentRange = investmentRange; }

    public List<PastInvestment> getPastInvestments() { return pastInvestments; }
    public void setPastInvestments(List<PastInvestment> pastInvestments) { this.pastInvestments = pastInvestments; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public LocalDateTime getVerificationDate() { return verificationDate; }
    public void setVerificationDate(LocalDateTime verificationDate) { this.verificationDate = verificationDate; }
}