package com.klef.ms.sdp.controller;

import com.klef.ms.sdp.dto.BidRequest;
import com.klef.ms.sdp.dto.BidResponse;
import com.klef.ms.sdp.service.BiddingService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bids")
public class BiddingController {

    private final BiddingService biddingService;

    // Constructor injection
    public BiddingController(BiddingService biddingService) {
        this.biddingService = biddingService;
    }

    // ==========================================
    // PLACE BID
    // ==========================================

    @PostMapping
    public ResponseEntity<BidResponse> placeBid(
            @Valid @RequestBody BidRequest request) {

        BidResponse response = biddingService.placeBid(request);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    // ==========================================
    // GET BID BY ID
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<BidResponse> getBidById(
            @PathVariable Long id) {

        BidResponse response =
                biddingService.getBidById(id);

        return ResponseEntity.ok(response);
    }

    // ==========================================
    // GET ALL BIDS FOR AUCTION
    // ==========================================

    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<List<BidResponse>> getBidsByAuction(
            @PathVariable Long auctionId) {

        List<BidResponse> response =
                biddingService.getBidsByAuction(auctionId);

        return ResponseEntity.ok(response);
    }

    // ==========================================
    // GET HIGHEST BID
    // ==========================================

    @GetMapping("/auction/{auctionId}/highest")
    public ResponseEntity<BidResponse> getHighestBid(
            @PathVariable Long auctionId) {

        BidResponse response =
                biddingService.getHighestBid(auctionId);

        return ResponseEntity.ok(response);
    }
}