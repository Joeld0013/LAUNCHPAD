package com.launchpad.dto;

import com.launchpad.model.Conversation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateConversationRequest {
    private String investorId;
    private String startupId;
    private String bidId; // Optional
    private Conversation.ConversationType type;
    private String initialMessage; // Optional initial message
}
