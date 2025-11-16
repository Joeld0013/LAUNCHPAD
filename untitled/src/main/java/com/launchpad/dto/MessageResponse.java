package com.launchpad.dto;

import com.launchpad.model.Conversation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

// ===== MESSAGE RESPONSE =====
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private String id;
    private String senderId;
    private String senderType;
    private String content;
    private Conversation.MessageType messageType;
    private LocalDateTime timestamp;
    private boolean isRead;
    private String attachmentUrl;
    private String attachmentName;
}