package com.klef.ms.sdp.service;

import java.util.List;

import com.klef.ms.sdp.dto.PaymentRequest;
import com.klef.ms.sdp.dto.PaymentResponse;

public interface PaymentService {

    PaymentResponse makePayment(PaymentRequest request);

    List<PaymentResponse> getAllPayments();

    PaymentResponse getPaymentByAuctionId(Long auctionId);
}