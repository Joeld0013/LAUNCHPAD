    package com.launchpad.services;

    import com.launchpad.dto.*;
    import com.launchpad.model.Conversation;
    import com.launchpad.model.Conversation.Message;
    import com.launchpad.model.User;
    import com.launchpad.repository.ConversationRepository;
    import com.launchpad.repository.UserRepository;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.time.LocalDateTime;
    import java.util.List;
    import java.util.UUID;
    import java.util.stream.Collectors;

    @Service
    @RequiredArgsConstructor
    @Slf4j
    public class ChatService {

        private final ConversationRepository conversationRepository;
        private final UserRepository userRepository;
        private final NotificationService notificationService;

        /**
         * Create or return existing conversation between startup and investor
         */
        @Transactional
        public ConversationResponse createOrGetConversation(CreateConversationRequest request, String currentUserId) {
            log.info("createOrGetConversation: startupId={}, investorId={}, bidId={}, type={}",
                    request.getStartupId(), request.getInvestorId(), request.getBidId(), request.getType());

            // Validate request ids
            if (request.getStartupId() == null || request.getInvestorId() == null) {
                throw new IllegalArgumentException("startupId and investorId are required");
            }

            // Try existing conversation first
            var existingOpt = conversationRepository.findByStartupIdAndInvestorId(request.getStartupId(), request.getInvestorId());
            if (existingOpt.isPresent()) {
                log.debug("Existing conversation found: {}", existingOpt.get().getId());
                return mapToConversationResponse(existingOpt.get(), currentUserId);
            }

            // Load users (throws if missing)
            User startup = userRepository.findById(request.getStartupId())
                    .orElseThrow(() -> new RuntimeException("Startup not found"));
            User investor = userRepository.findById(request.getInvestorId())
                    .orElseThrow(() -> new RuntimeException("Investor not found"));

            // Build conversation
            Conversation conversation = new Conversation();
            conversation.setStartupId(startup.getId());
            conversation.setStartupName(nullSafe(startup.getCompanyName(), "Startup"));
            conversation.setStartupAvatar(getInitials(conversation.getStartupName()));

            conversation.setInvestorId(investor.getId());
            conversation.setInvestorName(nullSafe(investor.getCompanyName(), "Investor"));
            conversation.setInvestorAvatar(getInitials(conversation.getInvestorName()));

            conversation.setBidId(request.getBidId());
            conversation.setType(request.getType() != null ? request.getType() : Conversation.ConversationType.DIRECT);

            // Startup status: if profile message then pending, else active
            if (conversation.getType() == Conversation.ConversationType.PROFILE_MESSAGE) {
                conversation.setStartupStatus(Conversation.ConversationStatus.PENDING);
            } else {
                conversation.setStartupStatus(Conversation.ConversationStatus.ACTIVE);
            }
            // investor status default ACTIVE (model default)

            // Add a system message describing conversation start
            String systemMessage = getSystemMessage(conversation.getType(), conversation.getInvestorName());
            addSystemMessage(conversation, systemMessage);

            // If an initial message provided, add it from currentUserId
            if (request.getInitialMessage() != null && !request.getInitialMessage().trim().isEmpty()) {
                // Validate currentUser is either startup or investor
                String senderType = resolveUserTypeStrict(currentUserId, conversation);
                addMessage(conversation, request.getInitialMessage().trim(), currentUserId, senderType,
                        Conversation.MessageType.TEXT, null, null);
                // set unread for the recipient of this initial message (done inside addMessage)
            }

            // Save and notify
            conversation.setCreatedAt(LocalDateTime.now());
            conversation.setUpdatedAt(LocalDateTime.now());
            Conversation saved = conversationRepository.save(conversation);

            // Choose who to notify: if conversation is PENDING (investor -> startup) notify startup; else notify recipient depending on initial message sender
            String notifyRecipientId = request.getInitialMessage() != null && !request.getInitialMessage().isBlank()
                    ? (resolveUserTypeStrict(currentUserId, saved).equals("STARTUP") ? saved.getInvestorId() : saved.getStartupId())
                    : saved.getStartupId(); // default notify startup (investor started)

            String notifyMessage = "New message from " + saved.getInvestorName();
            notificationService.sendChatNotification(notifyRecipientId, notifyMessage, saved.getId());

            return mapToConversationResponse(saved, currentUserId);
        }

        /**
         * Send a message in an existing conversation
         */
        @Transactional
        public MessageResponse sendMessage(SendMessageRequest request, String currentUserId) {
            log.info("sendMessage: conversationId={}, fromUserId={}", request.getConversationId(), currentUserId);

            Conversation conversation = conversationRepository.findById(request.getConversationId())
                    .orElseThrow(() -> new RuntimeException("Conversation not found"));

            // Validate current user belongs to this conversation
            if (!currentUserId.equals(conversation.getStartupId()) && !currentUserId.equals(conversation.getInvestorId())) {
                throw new RuntimeException("Access denied to this conversation");
            }

            // If either party has BLOCKED the conversation, no messages allowed
            if (conversation.getStartupStatus() == Conversation.ConversationStatus.BLOCKED
                    || conversation.getInvestorStatus() == Conversation.ConversationStatus.BLOCKED) {
                throw new RuntimeException("This conversation has been blocked");
            }

            // If conversation is pending, only investor can send (investor initiated)
            if (conversation.getStartupStatus() == Conversation.ConversationStatus.PENDING
                    && !currentUserId.equals(conversation.getInvestorId())) {
                throw new RuntimeException("Cannot send message to a pending conversation");
            }

            // Determine sender type strictly (throws if not part of conv)
            String senderType = resolveUserTypeStrict(currentUserId, conversation);

            // Build message
            Message message = new Message();
            message.setId(UUID.randomUUID().toString());
            message.setSenderId(currentUserId);
            message.setSenderType(senderType);
            message.setContent(request.getContent());
            message.setMessageType(request.getMessageType() != null ? request.getMessageType() : Conversation.MessageType.TEXT);
            message.setAttachmentUrl(request.getAttachmentUrl());
            message.setAttachmentName(request.getAttachmentName());
            message.setTimestamp(LocalDateTime.now());
            message.setRead(false);

            // Add message and update metadata
            conversation.getMessages().add(message);
            conversation.setLastMessageText(message.getContent());
            conversation.setLastMessageTime(LocalDateTime.now());
            conversation.setLastMessageSenderId(currentUserId);
            conversation.setUpdatedAt(LocalDateTime.now());

            // Increment unread count for recipient safely
            if ("STARTUP".equals(senderType)) {
                incrementUnreadInvestor(conversation);
            } else {
                incrementUnreadStartup(conversation);
            }

            Conversation saved = conversationRepository.save(conversation);

            // Send notification to recipient
            String recipientId = "STARTUP".equals(senderType) ? saved.getInvestorId() : saved.getStartupId();
            String senderName = "STARTUP".equals(senderType) ? saved.getStartupName() : saved.getInvestorName();
            notificationService.sendChatNotification(recipientId, "New message from " + senderName, saved.getId());

            return mapToMessageResponse(message);
        }

        /**
         * Get conversation details and mark messages as read
         */
        public ConversationDetailResponse getConversationDetails(String conversationId, String currentUserId) {
            log.info("getConversationDetails: conversationId={}, user={}", conversationId, currentUserId);

            Conversation conversation = conversationRepository.findById(conversationId)
                    .orElseThrow(() -> new RuntimeException("Conversation not found"));

            // Access check
            if (!currentUserId.equals(conversation.getStartupId()) && !currentUserId.equals(conversation.getInvestorId())) {
                throw new RuntimeException("Access denied to this conversation");
            }

            // Mark unread messages as read for this user
            markMessagesAsRead(conversation, currentUserId);

            ConversationDetailResponse resp = new ConversationDetailResponse();
            resp.setId(conversation.getId());
            resp.setStartupId(conversation.getStartupId());
            resp.setStartupName(conversation.getStartupName());
            resp.setStartupAvatar(conversation.getStartupAvatar());
            resp.setInvestorId(conversation.getInvestorId());
            resp.setInvestorName(conversation.getInvestorName());
            resp.setInvestorAvatar(conversation.getInvestorAvatar());
            resp.setStatus(getUserStatus(conversation, currentUserId));
            resp.setEncrypted(conversation.isEncrypted());
            resp.setType(conversation.getType());
            resp.setBidId(conversation.getBidId());

            List<MessageResponse> messages = conversation.getMessages().stream()
                    .map(this::mapToMessageResponse)
                    .collect(Collectors.toList());
            resp.setMessages(messages);

            return resp;
        }

        /**
         * Get all conversations for a user, ordered by last message time desc
         */
        public List<ConversationResponse> getUserConversations(String userId, String userType) {
            log.info("getUserConversations: userId={}, userType={}", userId, userType);

            List<Conversation> conversations;
            if ("STARTUP".equalsIgnoreCase(userType)) {
                conversations = conversationRepository.findByStartupIdOrderByLastMessageTimeDesc(userId);
            } else {
                conversations = conversationRepository.findByInvestorIdOrderByLastMessageTimeDesc(userId);
            }

            return conversations.stream()
                    .map(conv -> mapToConversationResponse(conv, userId))
                    .collect(Collectors.toList());
        }

        /**
         * Get pending conversations for a startup
         */
        public List<ConversationResponse> getPendingConversations(String startupId) {
            log.info("getPendingConversations: startupId={}", startupId);
            List<Conversation> conversations = conversationRepository.findPendingConversationsByStartupId(startupId);
            return conversations.stream()
                    .map(conv -> mapToConversationResponse(conv, startupId))
                    .collect(Collectors.toList());
        }

        /**
         * Startup can accept or block a pending/active conversation
         */
        @Transactional
        public ConversationResponse updateConversationStatus(UpdateConversationStatusRequest request, String startupId) {
            log.info("updateConversationStatus: conversationId={}, newStatus={}, byStartup={}",
                    request.getConversationId(), request.getStatus(), startupId);

            Conversation conversation = conversationRepository.findById(request.getConversationId())
                    .orElseThrow(() -> new RuntimeException("Conversation not found"));

            // only startup may change its startupStatus
            if (!startupId.equals(conversation.getStartupId())) {
                throw new RuntimeException("Only startup can update conversation status");
            }

            conversation.setStartupStatus(request.getStatus());
            conversation.setUpdatedAt(LocalDateTime.now());

            // If startup blocks, reflect that in investorStatus (optional depending on business rules).
            if (request.getStatus() == Conversation.ConversationStatus.BLOCKED) {
                conversation.setInvestorStatus(Conversation.ConversationStatus.BLOCKED);
            } else if (request.getStatus() == Conversation.ConversationStatus.ACTIVE
                    && conversation.getInvestorStatus() == Conversation.ConversationStatus.BLOCKED) {
                // If previously blocked, revive investor status as ACTIVE
                conversation.setInvestorStatus(Conversation.ConversationStatus.ACTIVE);
            }

            // Add system message to record this event
            String systemMessage = request.getStatus() == Conversation.ConversationStatus.ACTIVE
                    ? "Conversation accepted by " + conversation.getStartupName()
                    : "Conversation blocked by " + conversation.getStartupName();
            addSystemMessage(conversation, systemMessage);

            Conversation saved = conversationRepository.save(conversation);

            // Notify investor when accepted
            if (request.getStatus() == Conversation.ConversationStatus.ACTIVE) {
                notificationService.sendChatNotification(
                        conversation.getInvestorId(),
                        conversation.getStartupName() + " accepted your message request",
                        conversation.getId()
                );
            } else if (request.getStatus() == Conversation.ConversationStatus.BLOCKED) {
                notificationService.sendChatNotification(
                        conversation.getInvestorId(),
                        conversation.getStartupName() + " has blocked the conversation",
                        conversation.getId()
                );
            }

            return mapToConversationResponse(saved, startupId);
        }

        /**
         * Get unread count across conversations for a user
         */
        public UnreadCountResponse getUnreadCount(String userId, String userType) {
            log.info("getUnreadCount: userId={}, userType={}", userId, userType);

            List<Conversation> conversations;
            if ("STARTUP".equalsIgnoreCase(userType)) {
                conversations = conversationRepository.findByStartupIdOrderByLastMessageTimeDesc(userId);
            } else {
                conversations = conversationRepository.findByInvestorIdOrderByLastMessageTimeDesc(userId);
            }

            int totalUnread;

            List<UnreadCountResponse.ConversationUnread> unreadList = conversations.stream()
                    .map(conv -> {
                        int u = "STARTUP".equalsIgnoreCase(userType)
                                ? safeInt(conv.getUnreadCountStartup())
                                : safeInt(conv.getUnreadCountInvestor());
                        return new UnreadCountResponse.ConversationUnread(conv.getId(), u);
                    })
                    .filter(c -> c.getUnread() > 0)
                    .collect(Collectors.toList());

// Now compute total unread
            totalUnread = unreadList.stream()
                    .mapToInt(UnreadCountResponse.ConversationUnread::getUnread)
                    .sum();

            return new UnreadCountResponse(totalUnread, unreadList);

        }

        // ===== Helper methods =====

        private void markMessagesAsRead(Conversation conversation, String userId) {
            boolean updated = false;

            String userType = resolveUserTypeStrict(userId, conversation);

            for (Message m : conversation.getMessages()) {
                if (!m.getSenderId().equals(userId) && !m.isRead()) {
                    m.setRead(true);
                    updated = true;
                }
            }

            if (updated) {
                if ("STARTUP".equals(userType)) {
                    conversation.setUnreadCountStartup(0);
                } else {
                    conversation.setUnreadCountInvestor(0);
                }
                conversation.setUpdatedAt(LocalDateTime.now());
                conversationRepository.save(conversation);
            }
        }

        private void addSystemMessage(Conversation conversation, String content) {
            Message systemMessage = new Message();
            systemMessage.setId(UUID.randomUUID().toString());
            systemMessage.setSenderId("SYSTEM");
            systemMessage.setSenderType("SYSTEM");
            systemMessage.setContent(content);
            systemMessage.setMessageType(Conversation.MessageType.SYSTEM);
            systemMessage.setTimestamp(LocalDateTime.now());
            systemMessage.setRead(true); // system messages considered read by both sides

            conversation.getMessages().add(systemMessage);
            conversation.setLastMessageText(content);
            conversation.setLastMessageTime(LocalDateTime.now());
            conversation.setUpdatedAt(LocalDateTime.now());
        }

        private void addMessage(Conversation conversation,
                                String content,
                                String senderId,
                                String senderType,
                                Conversation.MessageType messageType,
                                String attachmentUrl,
                                String attachmentName) {

            Message msg = new Message();
            msg.setId(UUID.randomUUID().toString());
            msg.setSenderId(senderId);
            msg.setSenderType(senderType);
            msg.setContent(content);
            msg.setMessageType(messageType != null ? messageType : Conversation.MessageType.TEXT);
            msg.setAttachmentUrl(attachmentUrl);
            msg.setAttachmentName(attachmentName);
            msg.setTimestamp(LocalDateTime.now());
            msg.setRead(false);

            conversation.getMessages().add(msg);
            conversation.setLastMessageText(msg.getContent());
            conversation.setLastMessageTime(LocalDateTime.now());
            conversation.setUpdatedAt(LocalDateTime.now());

            // increment unread for recipient
            if ("STARTUP".equals(senderType)) {
                incrementUnreadInvestor(conversation);
            } else if ("INVESTOR".equals(senderType)) {
                incrementUnreadStartup(conversation);
            } // SYSTEM handled elsewhere
        }

        private void incrementUnreadStartup(Conversation conversation) {
            conversation.setUnreadCountStartup(safeInt(conversation.getUnreadCountStartup()) + 1);
        }

        private void incrementUnreadInvestor(Conversation conversation) {
            conversation.setUnreadCountInvestor(safeInt(conversation.getUnreadCountInvestor()) + 1);
        }

        private int safeInt(Integer value) {
            return value == null ? 0 : value;
        }

        private String getSystemMessage(Conversation.ConversationType type, String investorName) {
            if (type == null) return "Conversation started";
            switch (type) {
                case BID_NEGOTIATION:
                    return nullSafe(investorName, "Investor") + " started a conversation to discuss the bid";
                case PROFILE_MESSAGE:
                    return nullSafe(investorName, "Investor") + " sent you a message. Accept to continue the conversation";
                default:
                    return "Conversation started";
            }
        }

        // Resolve user type strictly; throws if user not part of this conversation
        private String resolveUserTypeStrict(String userId, Conversation conversation) {
            if (userId.equals(conversation.getStartupId())) return "STARTUP";
            if (userId.equals(conversation.getInvestorId())) return "INVESTOR";
            throw new RuntimeException("User is not part of this conversation");
        }

        private String nullSafe(String val, String fallback) {
            return val == null || val.trim().isEmpty() ? fallback : val;
        }

        private String getInitials(String name) {
            if (name == null || name.trim().isEmpty()) return "UN";
            String[] parts = name.trim().split("\\s+");
            if (parts.length == 1) {
                return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
            } else {
                return ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
            }
        }

        private Conversation.ConversationStatus getUserStatus(Conversation conversation, String userId) {
            return userId.equals(conversation.getStartupId())
                    ? conversation.getStartupStatus()
                    : conversation.getInvestorStatus();
        }

        private ConversationResponse mapToConversationResponse(Conversation conversation, String currentUserId) {
            ConversationResponse response = new ConversationResponse();
            response.setId(conversation.getId());
            response.setStartupId(conversation.getStartupId());
            response.setStartupName(conversation.getStartupName());
            response.setStartupAvatar(conversation.getStartupAvatar());
            response.setInvestorId(conversation.getInvestorId());
            response.setInvestorName(conversation.getInvestorName());
            response.setInvestorAvatar(conversation.getInvestorAvatar());
            response.setLastMessageText(conversation.getLastMessageText());
            response.setLastMessageTime(conversation.getLastMessageTime());
            response.setLastMessageSenderId(conversation.getLastMessageSenderId());
            response.setStatus(getUserStatus(conversation, currentUserId));
            response.setEncrypted(conversation.isEncrypted());
            response.setType(conversation.getType());
            response.setBidId(conversation.getBidId());

            int unreadCount = currentUserId.equals(conversation.getStartupId())
                    ? safeInt(conversation.getUnreadCountStartup())
                    : safeInt(conversation.getUnreadCountInvestor());
            response.setUnreadCount(unreadCount);

            return response;
        }

        private MessageResponse mapToMessageResponse(Message message) {
            MessageResponse resp = new MessageResponse();
            resp.setId(message.getId());
            resp.setSenderId(message.getSenderId());
            resp.setSenderType(message.getSenderType());
            resp.setContent(message.getContent());
            resp.setMessageType(message.getMessageType());
            resp.setTimestamp(message.getTimestamp());
            resp.setRead(message.isRead());
            resp.setAttachmentUrl(message.getAttachmentUrl());
            resp.setAttachmentName(message.getAttachmentName());
            return resp;
        }
    }
