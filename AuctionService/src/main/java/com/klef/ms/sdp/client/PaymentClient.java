package com.klef.ms.sdp.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.math.BigDecimal;

@FeignClient(name = "PAYMENT-SERVICE")
public interface PaymentClient {

    @PostMapping("/api/payments")
    Object createPayment(
            @RequestParam("auctionId") Long auctionId,
            @RequestParam("userId") Long userId,
            @RequestParam("amount") BigDecimal amount
    );
}