package com.launchpad.admin.model;

public class PastInvestment {
    private String name;
    private String amount;
    private String year;
    private String description;

    public PastInvestment() {}

    public PastInvestment(String name, String amount, String year, String description) {
        this.name = name;
        this.amount = amount;
        this.year = year;
        this.description = description;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}