package com.launchpad.dto;

public class CreateBidRequest {
    private String startupId;
    private Double amount;
    private Double equity;
    private String bidType;
    private String message;

    public CreateBidRequest() {}

    public String getStartupId() { return startupId; }
    public void setStartupId(String startupId) { this.startupId = startupId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public Double getEquity() { return equity; }
    public void setEquity(Double equity) { this.equity = equity; }

    public String getBidType() { return bidType; }
    public void setBidType(String bidType) { this.bidType = bidType; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}