package com.launchpad.dto;

import java.time.LocalDateTime;

public class BidDTO {
    private String id;
    private String startupId;
    private String startupName;
    private String investorId;
    private String investorName;
    private Double amount;
    private Double equity;
    private String bidType;
    private String message;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public BidDTO() {}

    public BidDTO(String startupId, String startupName, String investorId, String investorName,
                  Double amount, Double equity, String bidType, String message, String status,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.startupId = startupId;
        this.startupName = startupName;
        this.investorId = investorId;
        this.investorName = investorName;
        this.amount = amount;
        this.equity = equity;
        this.bidType = bidType;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStartupId() { return startupId; }
    public void setStartupId(String startupId) { this.startupId = startupId; }

    public String getStartupName() { return startupName; }
    public void setStartupName(String startupName) { this.startupName = startupName; }

    public String getInvestorId() { return investorId; }
    public void setInvestorId(String investorId) { this.investorId = investorId; }

    public String getInvestorName() { return investorName; }
    public void setInvestorName(String investorName) { this.investorName = investorName; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public Double getEquity() { return equity; }
    public void setEquity(Double equity) { this.equity = equity; }

    public String getBidType() { return bidType; }
    public void setBidType(String bidType) { this.bidType = bidType; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}