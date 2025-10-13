package com.launchpad.admin.dto;

import javax.validation.constraints.NotBlank;

public class ApprovalRequestDTO {

    @NotBlank(message = "Startup ID is required")
    private String startupId;

    private String comments;
    private String adminEmail;

    // Constructors
    public ApprovalRequestDTO() {}

    public ApprovalRequestDTO(String startupId, String comments) {
        this.startupId = startupId;
        this.comments = comments;
    }

    // Getters and Setters
    public String getStartupId() {
        return startupId;
    }

    public void setStartupId(String startupId) {
        this.startupId = startupId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }
}