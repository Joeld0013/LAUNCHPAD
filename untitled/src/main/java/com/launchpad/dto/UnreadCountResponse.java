package com.launchpad.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnreadCountResponse {
    private int totalUnread;
    private List<ConversationUnread> conversations;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConversationUnread {
        private String conversationId;
        private int unread;   // FIXED: renamed from unreadCount to unread
    }
}
