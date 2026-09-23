package com.klef.ms.sdp.client;

import com.klef.ms.sdp.dto.AuctionResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@FeignClient(name = "AUCTION-SERVICE")
public interface AuctionClient {

    @GetMapping("/api/auctions/{id}")
    AuctionResponse getAuctionById(
            @PathVariable("id") Long id
    );

    @GetMapping("/api/auctions/{id}/validate-bid")
    Boolean validateBid(
            @PathVariable("id") Long id,
            @RequestParam("bidAmount") BigDecimal bidAmount
    );

    @PutMapping("/api/auctions/{id}/bid")
    String updateHighestBid(
            @PathVariable("id") Long id,
            @RequestParam("bidderId") Long bidderId,
            @RequestParam("bidAmount") BigDecimal bidAmount
    );
}