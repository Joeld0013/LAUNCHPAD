package com.launchpad.repository;

import com.launchpad.model.Bid;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends MongoRepository<Bid, String> {

    // All bids for a startup
    List<Bid> findByStartupIdOrderByCreatedAtDesc(String startupId);

    // All bids from an investor
    List<Bid> findByInvestorIdOrderByCreatedAtDesc(String investorId);

    // Bids by status for a startup
    List<Bid> findByStartupIdAndStatusOrderByCreatedAtDesc(String startupId, String status);

    // Bids by status for an investor
    List<Bid> findByInvestorIdAndStatusOrderByCreatedAtDesc(String investorId, String status);
}
