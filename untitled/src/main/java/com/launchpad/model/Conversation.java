package com.launchpad.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "conversations")
public class Conversation {
    @Id
    private String id;

    private String startupId;
    private String startupName;
    private String startupAvatar;

    private String investorId;
    private String investorName;
    private String investorAvatar;

    private List<Message> messages = new ArrayList<>();

    private String lastMessageText;
    private LocalDateTime lastMessageTime;
    private String lastMessageSenderId;

    // Status for startups: PENDING, ACTIVE, BLOCKED
    private ConversationStatus startupStatus = ConversationStatus.PENDING;

    // Status for investors: always ACTIVE unless blocked by startup
    private ConversationStatus investorStatus = ConversationStatus.ACTIVE;

    private String bidId; // Reference to bid if conversation started from bid
    private ConversationType type; // BID_NEGOTIATION, PROFILE_MESSAGE, DIRECT

    private int unreadCountStartup = 0;
    private int unreadCountInvestor = 0;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    private boolean isEncrypted = true; // E2E encryption flag

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String id;
        private String senderId;
        private String senderType; // STARTUP or INVESTOR
        private String content;
        private MessageType messageType = MessageType.TEXT;
        private LocalDateTime timestamp = LocalDateTime.now();
        private boolean isRead = false;
        private String attachmentUrl;
        private String attachmentName;
    }

    public enum ConversationStatus {
        PENDING,    // Startup hasn't accepted yet (for profile messages)
        ACTIVE,     // Normal conversation
        BLOCKED     // Startup blocked the investor
    }

    public enum ConversationType {
        BID_NEGOTIATION,    // Started from bid negotiation
        PROFILE_MESSAGE,    // Investor messaged from profile
        DIRECT              // Direct message
    }

    public enum MessageType {
        TEXT,
        ATTACHMENT,
        SYSTEM  // System messages like "Bid accepted", "Conversation started"
    }
}