// ===== CONVERSATION REPOSITORY =====
package com.launchpad.repository;

import com.launchpad.model.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends MongoRepository<Conversation, String> {

    // Find conversation between startup and investor
    Optional<Conversation> findByStartupIdAndInvestorId(String startupId, String investorId);

    // Find all conversations for a startup
    List<Conversation> findByStartupIdOrderByLastMessageTimeDesc(String startupId);

    // Find all conversations for an investor
    List<Conversation> findByInvestorIdOrderByLastMessageTimeDesc(String investorId);

    // Find conversation by bid ID
    Optional<Conversation> findByBidId(String bidId);

    // Find all pending conversations for a startup (profile messages not yet accepted)
    @Query("{ 'startupId': ?0, 'startupStatus': 'PENDING' }")
    List<Conversation> findPendingConversationsByStartupId(String startupId);

    // Find all active conversations for a startup
    @Query("{ 'startupId': ?0, 'startupStatus': 'ACTIVE' }")
    List<Conversation> findActiveConversationsByStartupId(String startupId);

    // Count unread messages for startup
    @Query(value = "{ 'startupId': ?0, 'unreadCountStartup': { $gt: 0 } }", count = true)
    long countUnreadConversationsForStartup(String startupId);

    // Count unread messages for investor
    @Query(value = "{ 'investorId': ?0, 'unreadCountInvestor': { $gt: 0 } }", count = true)
    long countUnreadConversationsForInvestor(String investorId);
}

