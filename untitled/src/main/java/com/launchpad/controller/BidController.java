package com.launchpad.controller;

import com.launchpad.model.Bid;
import com.launchpad.services.BidService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/bids")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:5500"})
public class BidController {

    private final BidService bidService;

    // -----------------------------
    // ACCEPT BID
    // -----------------------------
    @PostMapping("/{bidId}/accept")
    public ResponseEntity<?> acceptBid(
            @PathVariable Long bidId,
            Authentication authentication) {

        try {
            String startupId = authentication.getName();
            log.info("Accepting bid {} by startup {}", bidId, startupId);

            Bid bid = bidService.acceptBid(String.valueOf(bidId), startupId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Bid accepted successfully. A conversation has been created.");
            response.put("bid", bid);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("Error accepting bid: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            log.error("Unexpected error accepting bid", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error accepting bid"));
        }
    }

    // -----------------------------
    // NEGOTIATE BID
    // -----------------------------
    @PostMapping("/{bidId}/negotiate")
    public ResponseEntity<?> negotiateBid(
            @PathVariable Long bidId,
            Authentication authentication) {

        try {
            String startupId = authentication.getName();
            log.info("Starting negotiation for bid {} by startup {}", bidId, startupId);

            Bid bid = bidService.negotiateBid(String.valueOf(bidId), startupId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Negotiation started. A conversation has been created.");
            response.put("bid", bid);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("Error starting negotiation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            log.error("Unexpected error starting negotiation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error starting negotiation"));
        }
    }

    // -----------------------------
    // REJECT BID
    // -----------------------------
    @PostMapping("/{bidId}/reject")
    public ResponseEntity<?> rejectBid(
            @PathVariable Long bidId,
            Authentication authentication) {

        try {
            String startupId = authentication.getName();
            log.info("Rejecting bid {} by startup {}", bidId, startupId);

            Bid bid = bidService.rejectBid(String.valueOf(bidId), startupId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Bid rejected successfully.");
            response.put("bid", bid);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("Error rejecting bid: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            log.error("Unexpected error rejecting bid", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error rejecting bid"));
        }
    }

    // -----------------------------
    // WITHDRAW BID (Investor)
    // -----------------------------
    @PostMapping("/{bidId}/withdraw")
    public ResponseEntity<?> withdrawBid(
            @PathVariable Long bidId,
            Authentication authentication) {

        try {
            String investorId = authentication.getName();
            log.info("Withdrawing bid {} by investor {}", bidId, investorId);

            Bid bid = bidService.withdrawBid(String.valueOf(bidId), investorId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Bid withdrawn successfully.");
            response.put("bid", bid);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("Error withdrawing bid: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            log.error("Unexpected error withdrawing bid", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error withdrawing bid"));
        }
    }
}
