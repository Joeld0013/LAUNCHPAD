package com.launchpad.services;

import com.launchpad.dto.BidDTO;
import com.launchpad.model.Bid;
import com.launchpad.model.Investor;
import com.launchpad.model.Startup;
import com.launchpad.repository.BidRepository;
import com.launchpad.repository.InvestorRepository;
import com.launchpad.repository.StartupProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BidService {

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private InvestorRepository investorRepository;

    @Autowired
    private StartupProfileRepository startupRepository;

    // Create a new bid
    public BidDTO createBid(String investorId, String startupId, Double amount, Double equity,
                            String bidType, String message) throws Exception {

        if (amount == null || equity == null || amount <= 0 || equity <= 0 || equity > 100) {
            throw new Exception("Invalid bid amount or equity percentage");
        }

        Investor investor = investorRepository.findById(investorId)
                .orElseThrow(() -> new Exception("Investor not found"));

        Startup startup = startupRepository.findById(startupId)
                .orElseThrow(() -> new Exception("Startup not found"));

        Bid bid = new Bid(startupId, investorId, amount, equity, bidType, message);

        if (bid.getStatus() == null) {
            bid.setStatus("PENDING");
        }
        if (bid.getCreatedAt() == null) {
            bid.setCreatedAt(LocalDateTime.now());
        }
        bid.setUpdatedAt(LocalDateTime.now());

        Bid savedBid = bidRepository.save(bid);

        return convertToBidDTO(
                savedBid,
                startup.getName() != null ? startup.getName() : "Unknown Startup",
                investor.getName() != null ? investor.getName() : "Unknown Investor"
        );
    }

    // Get all bids for an investor
    public List<BidDTO> getInvestorBids(String investorId) throws Exception {
        List<Bid> bids = bidRepository.findByInvestorIdOrderByCreatedAtDesc(investorId);

        return bids.stream().map(bid -> {
            try {
                Startup startup = startupRepository.findById(bid.getStartupId())
                        .orElseThrow(() -> new Exception("Startup not found"));
                Investor investor = investorRepository.findById(bid.getInvestorId())
                        .orElseThrow(() -> new Exception("Investor not found"));

                return convertToBidDTO(
                        bid,
                        startup.getName() != null ? startup.getName() : "Unknown Startup",
                        investor.getName() != null ? investor.getName() : "Unknown Investor"
                );
            } catch (Exception e) {
                return null;
            }
        }).filter(dto -> dto != null).collect(Collectors.toList());
    }

    // Get all bids for a startup (received bids)
    public List<BidDTO> getStartupBids(String startupId) throws Exception {
        List<Bid> bids = bidRepository.findByStartupIdOrderByCreatedAtDesc(startupId);

        return bids.stream().map(bid -> {
            try {
                Startup startup = startupRepository.findById(bid.getStartupId())
                        .orElseThrow(() -> new Exception("Startup not found"));
                Investor investor = investorRepository.findById(bid.getInvestorId())
                        .orElseThrow(() -> new Exception("Investor not found"));

                return convertToBidDTO(
                        bid,
                        startup.getName() != null ? startup.getName() : "Unknown Startup",
                        investor.getName() != null ? investor.getName() : "Unknown Investor"
                );
            } catch (Exception e) {
                return null;
            }
        }).filter(dto -> dto != null).collect(Collectors.toList());
    }

    // Get bids by status for investor
    public List<BidDTO> getInvestorBidsByStatus(String investorId, String status) throws Exception {
        List<Bid> bids =
                bidRepository.findByInvestorIdAndStatusOrderByCreatedAtDesc(investorId, status);

        return bids.stream().map(bid -> {
            try {
                Startup startup = startupRepository.findById(bid.getStartupId())
                        .orElseThrow(() -> new Exception("Startup not found"));
                Investor investor = investorRepository.findById(bid.getInvestorId())
                        .orElseThrow(() -> new Exception("Investor not found"));

                return convertToBidDTO(
                        bid,
                        startup.getName() != null ? startup.getName() : "Unknown Startup",
                        investor.getName() != null ? investor.getName() : "Unknown Investor"
                );
            } catch (Exception e) {
                return null;
            }
        }).filter(dto -> dto != null).collect(Collectors.toList());
    }

    // Get bids by status for startup
    public List<BidDTO> getStartupBidsByStatus(String startupId, String status) throws Exception {
        List<Bid> bids =
                bidRepository.findByStartupIdAndStatusOrderByCreatedAtDesc(startupId, status);

        return bids.stream().map(bid -> {
            try {
                Startup startup = startupRepository.findById(bid.getStartupId())
                        .orElseThrow(() -> new Exception("Startup not found"));
                Investor investor = investorRepository.findById(bid.getInvestorId())
                        .orElseThrow(() -> new Exception("Investor not found"));

                return convertToBidDTO(
                        bid,
                        startup.getName() != null ? startup.getName() : "Unknown Startup",
                        investor.getName() != null ? investor.getName() : "Unknown Investor"
                );
            } catch (Exception e) {
                return null;
            }
        }).filter(dto -> dto != null).collect(Collectors.toList());
    }

    // Accept a bid (startup side)
    public BidDTO acceptBid(String bidId) throws Exception {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new Exception("Bid not found"));

        bid.setStatus("ACCEPTED");
        bid.setUpdatedAt(LocalDateTime.now());
        Bid updatedBid = bidRepository.save(bid);

        Startup startup = startupRepository.findById(updatedBid.getStartupId())
                .orElseThrow(() -> new Exception("Startup not found"));
        Investor investor = investorRepository.findById(updatedBid.getInvestorId())
                .orElseThrow(() -> new Exception("Investor not found"));

        return convertToBidDTO(
                updatedBid,
                startup.getName() != null ? startup.getName() : "Unknown Startup",
                investor.getName() != null ? investor.getName() : "Unknown Investor"
        );
    }

    // Reject a bid (startup side)
    public BidDTO rejectBid(String bidId) throws Exception {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new Exception("Bid not found"));

        bid.setStatus("REJECTED");
        bid.setUpdatedAt(LocalDateTime.now());
        Bid updatedBid = bidRepository.save(bid);

        Startup startup = startupRepository.findById(updatedBid.getStartupId())
                .orElseThrow(() -> new Exception("Startup not found"));
        Investor investor = investorRepository.findById(updatedBid.getInvestorId())
                .orElseThrow(() -> new Exception("Investor not found"));

        return convertToBidDTO(
                updatedBid,
                startup.getName() != null ? startup.getName() : "Unknown Startup",
                investor.getName() != null ? investor.getName() : "Unknown Investor"
        );
    }

    // Withdraw a bid (investor side)
    public BidDTO withdrawBid(String bidId) throws Exception {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new Exception("Bid not found"));

        bid.setStatus("WITHDRAWN");
        bid.setUpdatedAt(LocalDateTime.now());
        Bid updatedBid = bidRepository.save(bid);

        Startup startup = startupRepository.findById(updatedBid.getStartupId())
                .orElseThrow(() -> new Exception("Startup not found"));
        Investor investor = investorRepository.findById(updatedBid.getInvestorId())
                .orElseThrow(() -> new Exception("Investor not found"));

        return convertToBidDTO(
                updatedBid,
                startup.getName() != null ? startup.getName() : "Unknown Startup",
                investor.getName() != null ? investor.getName() : "Unknown Investor"
        );
    }

    // Start negotiation
    public BidDTO startNegotiation(String bidId) throws Exception {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new Exception("Bid not found"));

        bid.setStatus("NEGOTIATING");
        bid.setUpdatedAt(LocalDateTime.now());
        Bid updatedBid = bidRepository.save(bid);

        Startup startup = startupRepository.findById(updatedBid.getStartupId())
                .orElseThrow(() -> new Exception("Startup not found"));
        Investor investor = investorRepository.findById(updatedBid.getInvestorId())
                .orElseThrow(() -> new Exception("Investor not found"));

        return convertToBidDTO(
                updatedBid,
                startup.getName() != null ? startup.getName() : "Unknown Startup",
                investor.getName() != null ? investor.getName() : "Unknown Investor"
        );
    }

    // Helper method to convert Bid to DTO
    private BidDTO convertToBidDTO(Bid bid, String startupName, String investorName) {
        BidDTO dto = new BidDTO();
        dto.setId(bid.getId());
        dto.setStartupId(bid.getStartupId());
        dto.setStartupName(startupName);
        dto.setInvestorId(bid.getInvestorId());
        dto.setInvestorName(investorName);
        dto.setAmount(bid.getAmount());
        dto.setEquity(bid.getEquity());
        dto.setBidType(bid.getBidType());
        dto.setMessage(bid.getMessage());
        dto.setStatus(bid.getStatus());
        dto.setCreatedAt(bid.getCreatedAt());
        dto.setUpdatedAt(bid.getUpdatedAt());
        return dto;
    }
}
