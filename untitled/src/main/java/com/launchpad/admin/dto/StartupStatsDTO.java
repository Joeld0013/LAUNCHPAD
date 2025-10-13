package com.launchpad.admin.dto;

public class StartupStatsDTO {

    private long totalStartups;
    private long pendingApprovals;
    private long approvedStartups;
    private long rejectedStartups;

    // Constructors
    public StartupStatsDTO() {}

    // Getters and Setters
    public long getTotalStartups() {
        return totalStartups;
    }

    public void setTotalStartups(long totalStartups) {
        this.totalStartups = totalStartups;
    }

    public long getPendingApprovals() {
        return pendingApprovals;
    }

    public void setPendingApprovals(long pendingApprovals) {
        this.pendingApprovals = pendingApprovals;
    }

    public long getApprovedStartups() {
        return approvedStartups;
    }

    public void setApprovedStartups(long approvedStartups) {
        this.approvedStartups = approvedStartups;
    }

    public long getRejectedStartups() {
        return rejectedStartups;
    }

    public void setRejectedStartups(long rejectedStartups) {
        this.rejectedStartups = rejectedStartups;
    }
}