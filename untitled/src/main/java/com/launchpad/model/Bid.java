package com.launchpad.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "bids")
public class Bid {
    @Id
    private String id;

    private String investorId;
    private String investorName;
    private String investorAvatar;

    private String startupId;
    private String startupName;
    private String startupAvatar;

    private Double amount; // Investment amount
    private Double equityPercentage; // Equity offered
    private Double valuationCap; // Optional valuation cap

    private BidType type; // EQUITY, CONVERTIBLE_NOTE, SAFE
    private String message; // Optional message from investor

    private BidStatus status = BidStatus.PENDING;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    private LocalDateTime expiresAt; // Optional expiration date

    public enum BidStatus {
        PENDING,            // Awaiting startup response
        ACCEPTED,           // Startup accepted the bid
        REJECTED,           // Startup rejected the bid
        UNDER_NEGOTIATION,  // Currently being negotiated
        WITHDRAWN,          // Investor withdrew the bid
        EXPIRED             // Bid expired
    }

    public enum BidType {
        EQUITY,
        CONVERTIBLE_NOTE,
        SAFE
    }
}