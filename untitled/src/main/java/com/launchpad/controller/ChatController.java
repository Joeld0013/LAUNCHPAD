package com.launchpad.controller;

import com.launchpad.dto.*;
import com.launchpad.model.Conversation;
import com.launchpad.services.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:5500"})
public class ChatController {

    private final ChatService chatService;

    /**
     * Create or get a conversation between startup and investor
     * POST /api/chat/conversations
     */
    @PostMapping("/conversations")
    public ResponseEntity<?> createConversation(
            @RequestBody CreateConversationRequest request,
            Authentication authentication) {
        try {
            log.info("Creating conversation: {}", request);
            String currentUserId = authentication.getName();
            ConversationResponse response = chatService.createOrGetConversation(request, currentUserId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error creating conversation: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            log.error("Error creating conversation", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Get all conversations for current user
     * GET /api/chat/conversations?userType=STARTUP|INVESTOR
     */
    @GetMapping("/conversations")
    public ResponseEntity<?> getUserConversations(
            @RequestParam String userType,
            Authentication authentication) {
        try {
            String userId = authentication.getName();
            log.info("Getting conversations for user {} of type {}", userId, userType);
            List<ConversationResponse> conversations = chatService.getUserConversations(userId, userType);
            return ResponseEntity.ok(conversations);
        } catch (Exception e) {
            log.error("Error getting conversations", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to load conversations");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Get pending conversations for startup (messages not yet accepted)
     * GET /api/chat/conversations/pending
     */
    @GetMapping("/conversations/pending")
    public ResponseEntity<?> getPendingConversations(Authentication authentication) {
        try {
            String startupId = authentication.getName();
            log.info("Getting pending conversations for startup {}", startupId);
            List<ConversationResponse> conversations = chatService.getPendingConversations(startupId);
            return ResponseEntity.ok(conversations);
        } catch (Exception e) {
            log.error("Error getting pending conversations", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to load pending conversations");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Get conversation details with all messages
     * GET /api/chat/conversations/{conversationId}
     */
    @GetMapping("/conversations/{conversationId}")
    public ResponseEntity<?> getConversationDetails(
            @PathVariable String conversationId,
            Authentication authentication) {
        try {
            String userId = authentication.getName();
            log.info("Getting conversation details for {} by user {}", conversationId, userId);
            ConversationDetailResponse response = chatService.getConversationDetails(conversationId, userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error getting conversation details: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            log.error("Error getting conversation details", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to load conversation");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Send a message in a conversation
     * POST /api/chat/messages
     */
    @PostMapping("/messages")
    public ResponseEntity<?> sendMessage(
            @RequestBody SendMessageRequest request,
            Authentication authentication) {
        try {
            String currentUserId = authentication.getName();
            log.info("Sending message in conversation {}", request.getConversationId());
            MessageResponse response = chatService.sendMessage(request, currentUserId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error sending message: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            log.error("Error sending message", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to send message");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Update conversation status (accept or block) - Startup only
     * PUT /api/chat/conversations/{conversationId}/status
     */
    @PutMapping("/conversations/{conversationId}/status")
    public ResponseEntity<?> updateConversationStatus(
            @PathVariable String conversationId,
            @RequestParam Conversation.ConversationStatus status,
            Authentication authentication) {
        try {
            String startupId = authentication.getName();
            log.info("Updating conversation {} status to {}", conversationId, status);

            UpdateConversationStatusRequest request = new UpdateConversationStatusRequest();
            request.setConversationId(conversationId);
            request.setStatus(status);

            ConversationResponse response = chatService.updateConversationStatus(request, startupId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error updating conversation status: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        } catch (Exception e) {
            log.error("Error updating conversation status", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update status");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Get unread message count for current user
     * GET /api/chat/unread?userType=STARTUP|INVESTOR
     */
    @GetMapping("/unread")
    public ResponseEntity<?> getUnreadCount(
            @RequestParam String userType,
            Authentication authentication) {
        try {
            String userId = authentication.getName();
            log.info("Getting unread count for user {} of type {}", userId, userType);
            UnreadCountResponse response = chatService.getUnreadCount(userId, userType);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting unread count", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get unread count");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Start conversation from bid negotiation
     * POST /api/chat/conversations/from-bid
     */
    @PostMapping("/conversations/from-bid")
    public ResponseEntity<?> createConversationFromBid(
            @RequestParam String bidId,
            @RequestParam String startupId,
            @RequestParam String investorId,
            Authentication authentication) {
        try {
            String currentUserId = authentication.getName();
            log.info("Creating conversation from bid {} between startup {} and investor {}",
                    bidId, startupId, investorId);

            CreateConversationRequest request = new CreateConversationRequest();
            request.setStartupId(startupId);
            request.setInvestorId(investorId);
            request.setBidId(bidId);
            request.setType(Conversation.ConversationType.BID_NEGOTIATION);
            request.setInitialMessage("Let's discuss the terms of your bid.");

            ConversationResponse response = chatService.createOrGetConversation(request, currentUserId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating conversation from bid", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create conversation");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Start conversation from investor profile message
     * POST /api/chat/conversations/from-profile
     */
    @PostMapping("/conversations/from-profile")
    public ResponseEntity<?> createConversationFromProfile(
            @RequestParam String startupId,
            @RequestParam String message,
            Authentication authentication) {
        try {
            String investorId = authentication.getName();
            log.info("Investor {} messaging startup {} from profile", investorId, startupId);

            CreateConversationRequest request = new CreateConversationRequest();
            request.setStartupId(startupId);
            request.setInvestorId(investorId);
            request.setType(Conversation.ConversationType.PROFILE_MESSAGE);
            request.setInitialMessage(message);

            ConversationResponse response = chatService.createOrGetConversation(request, investorId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating conversation from profile", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to send message");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}