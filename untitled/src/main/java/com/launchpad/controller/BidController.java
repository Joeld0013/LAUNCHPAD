package com.launchpad.controller;

import com.launchpad.dto.BidDTO;
import com.launchpad.dto.CreateBidRequest;
import com.launchpad.services.BidService;
import com.launchpad.shared.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/bids")
@CrossOrigin(origins = "*")
public class BidController {
    @Autowired
    private BidService bidService;

    @Autowired
    private JwtUtil jwtUtil;

    // Create a new bid
    @PostMapping("/create")
    public ResponseEntity<?> createBid(
            @RequestHeader("Authorization") String token,
            @RequestBody CreateBidRequest request) {
        try {
            String investorId = jwtUtil.getUserIdFromToken(token.replace("Bearer ", ""));

            BidDTO bid = bidService.createBid(
                    investorId,
                    request.getStartupId(),
                    request.getAmount(),
                    request.getEquity(),
                    request.getBidType(),
                    request.getMessage()
            );

            return ResponseEntity.ok(bid);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Get all bids for investor
    @GetMapping("/investor")
    public ResponseEntity<?> getInvestorBids(
            @RequestHeader("Authorization") String token) {
        try {
            String investorId = jwtUtil.getUserIdFromToken(token.replace("Bearer ", ""));
            List<BidDTO> bids = bidService.getInvestorBids(investorId);
            return ResponseEntity.ok(bids);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Get all bids for startup (received bids)
    @GetMapping("/startup")
    public ResponseEntity<?> getStartupBids(
            @RequestHeader("Authorization") String token) {
        try {
            String startupId = jwtUtil.getUserIdFromToken(token.replace("Bearer ", ""));
            List<BidDTO> bids = bidService.getStartupBids(startupId);
            return ResponseEntity.ok(bids);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Get bids by status for investor
    @GetMapping("/investor/status/{status}")
    public ResponseEntity<?> getInvestorBidsByStatus(
            @RequestHeader("Authorization") String token,
            @PathVariable String status) {
        try {
            String investorId = jwtUtil.getUserIdFromToken(token.replace("Bearer ", ""));
            List<BidDTO> bids = bidService.getInvestorBidsByStatus(investorId, status.toUpperCase());
            return ResponseEntity.ok(bids);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Get bids by status for startup
    @GetMapping("/startup/status/{status}")
    public ResponseEntity<?> getStartupBidsByStatus(
            @RequestHeader("Authorization") String token,
            @PathVariable String status) {
        try {
            String startupId = jwtUtil.getUserIdFromToken(token.replace("Bearer ", ""));
            List<BidDTO> bids = bidService.getStartupBidsByStatus(startupId, status.toUpperCase());
            return ResponseEntity.ok(bids);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Accept a bid (startup)
    @PutMapping("/{bidId}/accept")
    public ResponseEntity<?> acceptBid(
            @RequestHeader("Authorization") String token,
            @PathVariable String bidId) {
        try {
            BidDTO bid = bidService.acceptBid(bidId);
            return ResponseEntity.ok(bid);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Reject a bid (startup)
    @PutMapping("/{bidId}/reject")
    public ResponseEntity<?> rejectBid(
            @RequestHeader("Authorization") String token,
            @PathVariable String bidId) {
        try {
            BidDTO bid = bidService.rejectBid(bidId);
            return ResponseEntity.ok(bid);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Withdraw a bid (investor)
    @PutMapping("/{bidId}/withdraw")
    public ResponseEntity<?> withdrawBid(
            @RequestHeader("Authorization") String token,
            @PathVariable String bidId) {
        try {
            BidDTO bid = bidService.withdrawBid(bidId);
            return ResponseEntity.ok(bid);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Start negotiation
    @PutMapping("/{bidId}/negotiate")
    public ResponseEntity<?> startNegotiation(
            @RequestHeader("Authorization") String token,
            @PathVariable String bidId) {
        try {
            BidDTO bid = bidService.startNegotiation(bidId);
            return ResponseEntity.ok(bid);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}