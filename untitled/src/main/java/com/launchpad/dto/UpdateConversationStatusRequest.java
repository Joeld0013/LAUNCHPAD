package com.launchpad.dto;

import com.launchpad.model.Conversation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

// ===== UPDATE CONVERSATION STATUS REQUEST =====
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateConversationStatusRequest {
    private String conversationId;
    private Conversation.ConversationStatus status; // ACTIVE or BLOCKED
}