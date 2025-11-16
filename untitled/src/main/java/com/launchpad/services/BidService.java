package com.launchpad.services;

import com.launchpad.dto.CreateConversationRequest;
import com.launchpad.model.Bid;
import com.launchpad.model.Conversation;
import com.launchpad.repository.BidRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class BidService {

    private final BidRepository bidRepository;
    private final ChatService chatService;
    private final NotificationService notificationService;

    /**
     * Handle bid acceptance - automatically create conversation
     */
    @Transactional
    public Bid acceptBid(String bidId, String startupId) {
        log.info("Accepting bid {} by startup {}", bidId, startupId);

        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new RuntimeException("Bid not found"));

        // Verify the startup owns this bid
        if (!bid.getStartupId().equals(startupId)) {
            throw new RuntimeException("Unauthorized to accept this bid");
        }

        // Update bid status
        bid.setStatus(Bid.BidStatus.ACCEPTED);
        bid.setUpdatedAt(LocalDateTime.now());
        Bid savedBid = bidRepository.save(bid);

        // Automatically create conversation for bid negotiation
        CreateConversationRequest conversationRequest = new CreateConversationRequest();
        conversationRequest.setStartupId(bid.getStartupId());
        conversationRequest.setInvestorId(bid.getInvestorId());
        conversationRequest.setBidId(bidId);
        conversationRequest.setType(Conversation.ConversationType.BID_NEGOTIATION);
        conversationRequest.setInitialMessage("Thank you for your bid! Let's discuss the terms.");

        try {
            chatService.createOrGetConversation(conversationRequest, startupId);
            log.info("Conversation created successfully for accepted bid {}", bidId);
        } catch (Exception e) {
            log.error("Error creating conversation for accepted bid", e);
            // Don't fail the bid acceptance if conversation creation fails
        }

        // Send notification to investor
        notificationService.sendBidNotification(
                bid.getInvestorId(),
                "Your bid has been accepted! Start a conversation to discuss terms.",
                bidId
        );

        return savedBid;
    }

    /**
     * Handle negotiate action - create conversation
     */
    @Transactional
    public Bid negotiateBid(String bidId, String startupId) {
        log.info("Starting negotiation for bid {} by startup {}", bidId, startupId);

        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new RuntimeException("Bid not found"));

        // Verify the startup owns this bid
        if (!bid.getStartupId().equals(startupId)) {
            throw new RuntimeException("Unauthorized to negotiate this bid");
        }

        // Update bid status
        bid.setStatus(Bid.BidStatus.UNDER_NEGOTIATION);
        bid.setUpdatedAt(LocalDateTime.now());
        Bid savedBid = bidRepository.save(bid);

        // Create conversation for negotiation
        CreateConversationRequest conversationRequest = new CreateConversationRequest();
        conversationRequest.setStartupId(bid.getStartupId());
        conversationRequest.setInvestorId(bid.getInvestorId());
        conversationRequest.setBidId(bidId);
        conversationRequest.setType(Conversation.ConversationType.BID_NEGOTIATION);
        conversationRequest.setInitialMessage("Let's negotiate the terms of your bid.");

        try {
            chatService.createOrGetConversation(conversationRequest, startupId);
            log.info("Negotiation conversation created successfully for bid {}", bidId);
        } catch (Exception e) {
            log.error("Error creating negotiation conversation", e);
            // Don't fail the negotiation if conversation creation fails
        }

        // Send notification to investor
        notificationService.sendBidNotification(
                bid.getInvestorId(),
                "The startup wants to negotiate your bid. Check your messages!",
                bidId
        );

        return savedBid;
    }

    /**
     * Handle bid rejection
     */
    @Transactional
    public Bid rejectBid(String bidId, String startupId) {
        log.info("Rejecting bid {} by startup {}", bidId, startupId);

        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new RuntimeException("Bid not found"));

        // Verify the startup owns this bid
        if (!bid.getStartupId().equals(startupId)) {
            throw new RuntimeException("Unauthorized to reject this bid");
        }

        // Update bid status
        bid.setStatus(Bid.BidStatus.REJECTED);
        bid.setUpdatedAt(LocalDateTime.now());
        Bid savedBid = bidRepository.save(bid);

        // Send notification to investor
        notificationService.sendBidNotification(
                bid.getInvestorId(),
                "Your bid has been rejected by the startup.",
                bidId
        );

        return savedBid;
    }

    /**
     * Handle bid withdrawal by investor
     */
    @Transactional
    public Bid withdrawBid(String bidId, String investorId) {
        log.info("Withdrawing bid {} by investor {}", bidId, investorId);

        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new RuntimeException("Bid not found"));

        // Verify the investor owns this bid
        if (!bid.getInvestorId().equals(investorId)) {
            throw new RuntimeException("Unauthorized to withdraw this bid");
        }

        // Update bid status
        bid.setStatus(Bid.BidStatus.WITHDRAWN);
        bid.setUpdatedAt(LocalDateTime.now());
        Bid savedBid = bidRepository.save(bid);

        // Send notification to startup
        notificationService.sendBidNotification(
                bid.getStartupId(),
                "The investor has withdrawn their bid.",
                bidId
        );

        return savedBid;
    }
}