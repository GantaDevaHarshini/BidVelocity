package com.klef.ms.sdp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuctionResponse {

    private Long id;
    private String title;
    private String description;
    private BigDecimal startingBid;
    private BigDecimal currentBid;
    private Long highestBidderId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private Long winnerId;
}