package com.launchpad.admin.dto;

public class VerificationRequest {
    private String investorId;
    private String action; // APPROVE or REJECT
    private String rejectionReason;

    public String getInvestorId() { return investorId; }
    public void setInvestorId(String investorId) { this.investorId = investorId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}