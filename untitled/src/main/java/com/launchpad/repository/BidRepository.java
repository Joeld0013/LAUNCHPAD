// ===== BID REPOSITORY =====
package com.launchpad.repository;

import com.launchpad.model.Bid;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository extends MongoRepository<Bid, String> {

    // Find all bids for a startup
    List<Bid> findByStartupIdOrderByCreatedAtDesc(String startupId);

    // Find all bids from an investor
    List<Bid> findByInvestorIdOrderByCreatedAtDesc(String investorId);

    // Find bids by status for a startup
    List<Bid> findByStartupIdAndStatusOrderByCreatedAtDesc(String startupId, Bid.BidStatus status);

    // Find bids by status for an investor
    List<Bid> findByInvestorIdAndStatusOrderByCreatedAtDesc(String investorId, Bid.BidStatus status);
}
