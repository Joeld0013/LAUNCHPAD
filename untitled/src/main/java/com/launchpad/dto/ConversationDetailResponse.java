package com.launchpad.dto;

import com.launchpad.model.Conversation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

// ===== CONVERSATION DETAIL RESPONSE =====
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDetailResponse {
    private String id;
    private String startupId;
    private String startupName;
    private String startupAvatar;
    private String investorId;
    private String investorName;
    private String investorAvatar;
    private Conversation.ConversationStatus status;
    private List<MessageResponse> messages;
    private boolean isEncrypted;
    private Conversation.ConversationType type;
    private String bidId;
}