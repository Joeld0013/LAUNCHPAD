package com.launchpad.dto;

import com.launchpad.model.Conversation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

// ===== SEND MESSAGE REQUEST =====
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {
    private String conversationId;
    private String content;
    private Conversation.MessageType messageType = Conversation.MessageType.TEXT;
    private String attachmentUrl;
    private String attachmentName;
}