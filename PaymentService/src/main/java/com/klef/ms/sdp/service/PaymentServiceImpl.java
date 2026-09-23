package com.klef.ms.sdp.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.klef.ms.sdp.dto.PaymentRequest;
import com.klef.ms.sdp.dto.PaymentResponse;
import com.klef.ms.sdp.entity.Payment;
import com.klef.ms.sdp.exception.ResourceNotFoundException;
import com.klef.ms.sdp.repository.PaymentRepository;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public PaymentResponse makePayment(PaymentRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Payment request cannot be null"
            );
        }

        if (request.getAuctionId() == null) {
            throw new IllegalArgumentException(
                    "Auction ID is required"
            );
        }

        if (request.getWinnerId() == null) {
            throw new IllegalArgumentException(
                    "Winner ID is required"
            );
        }

        if (request.getAmount() == null ||
                request.getAmount().signum() <= 0) {

            throw new IllegalArgumentException(
                    "Payment amount must be greater than zero"
            );
        }

        Payment payment = new Payment();

        payment.setAuctionId(request.getAuctionId());
        payment.setWinnerId(request.getWinnerId());
        payment.setAmount(request.getAmount());

        /*
         * For this project we simulate a successful payment.
         */
        payment.setStatus("SUCCESS");

        payment.setPaymentDate(LocalDateTime.now());

        Payment savedPayment =
                paymentRepository.save(payment);

        return convertToResponse(savedPayment);
    }

    @Override
    public List<PaymentResponse> getAllPayments() {

        return paymentRepository
                .findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public PaymentResponse getPaymentByAuctionId(Long auctionId) {

        Payment payment = paymentRepository
                .findByAuctionId(auctionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Payment not found for auction ID: "
                                        + auctionId
                        )
                );

        return convertToResponse(payment);
    }

    private PaymentResponse convertToResponse(Payment payment) {

        return new PaymentResponse(
                payment.getId(),
                payment.getAuctionId(),
                payment.getWinnerId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getPaymentDate()
        );
    }
}