package com.launchpad.admin.dto;

public class AdminLoginResponse {

    private String message;
    private String token;
    private String adminEmail;
    private boolean success;

    // Constructors
    public AdminLoginResponse() {}

    public AdminLoginResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public AdminLoginResponse(String message, String token, String adminEmail, boolean success) {
        this.message = message;
        this.token = token;
        this.adminEmail = adminEmail;
        this.success = success;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}