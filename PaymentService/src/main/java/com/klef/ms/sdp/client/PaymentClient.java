package com.klef.ms.sdp.client;

import java.math.BigDecimal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.klef.ms.sdp.dto.PaymentRequest;
import com.klef.ms.sdp.dto.PaymentResponse;

@FeignClient(name = "PaymentService")
public interface PaymentClient {

    @PostMapping("/api/payments")
    PaymentResponse createPayment(
            @RequestBody PaymentRequest request
    );
}