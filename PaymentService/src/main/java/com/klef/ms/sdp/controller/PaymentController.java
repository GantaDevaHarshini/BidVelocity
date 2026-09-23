package com.klef.ms.sdp.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.klef.ms.sdp.dto.PaymentRequest;
import com.klef.ms.sdp.dto.PaymentResponse;
import com.klef.ms.sdp.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /*
     * This endpoint is called by AuctionService using Feign.
     *
     * POST /api/payments?auctionId=1&winnerId=10&amount=5000
     */
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(

            @RequestParam("auctionId")
            Long auctionId,

            @RequestParam("winnerId")
            Long winnerId,

            @RequestParam("amount")
            BigDecimal amount) {

        PaymentRequest request = new PaymentRequest();

        request.setAuctionId(auctionId);
        request.setWinnerId(winnerId);
        request.setAmount(amount);

        PaymentResponse response =
                paymentService.makePayment(request);

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
        );
    }

    /*
     * Get all payments
     */
    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {

        return ResponseEntity.ok(
                paymentService.getAllPayments()
        );
    }

    /*
     * Get payment by auction ID
     */
    @GetMapping("/auction/{auctionId}")
    public ResponseEntity<PaymentResponse> getPaymentByAuctionId(
            @PathVariable Long auctionId) {

        return ResponseEntity.ok(
                paymentService.getPaymentByAuctionId(auctionId)
        );
    }
}