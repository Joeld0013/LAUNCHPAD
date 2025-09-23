package com.launchpad.registration.dto;

public class StartupRegistrationRequest {
    private String name;
    private String email;
    private String phone;
    private String password; // ✅ Add password field
    private String country;
    private String address;
    private String industry;
    private String stage;
    private String description;
    private String website; // ✅ Add website field
    private String docType; // ✅ Add document type field

    // getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; } // ✅ Add getter
    public void setPassword(String password) { this.password = password; } // ✅ Add setter

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

    public String getWebsite() { return website; } // ✅ Add getter
    public void setWebsite(String website) { this.website = website; } // ✅ Add setter

    public String getDocType() { return docType; } // ✅ Add getter
    public void setDocType(String docType) { this.docType = docType; } // ✅ Add setter
}