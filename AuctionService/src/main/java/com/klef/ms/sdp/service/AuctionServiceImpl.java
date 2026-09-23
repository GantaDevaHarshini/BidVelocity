package com.klef.ms.sdp.service;

import com.klef.ms.sdp.client.PaymentClient;
import com.klef.ms.sdp.dto.AuctionRequest;
import com.klef.ms.sdp.dto.AuctionResponse;
import com.klef.ms.sdp.dto.WinnerResponse;
import com.klef.ms.sdp.entity.Auction;
import com.klef.ms.sdp.exception.ResourceNotFoundException;
import com.klef.ms.sdp.repository.AuctionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuctionServiceImpl implements AuctionService {

    private final AuctionRepository auctionRepository;
    private final PaymentClient paymentClient;

    public AuctionServiceImpl(
            AuctionRepository auctionRepository,
            PaymentClient paymentClient) {

        this.auctionRepository = auctionRepository;
        this.paymentClient = paymentClient;
    }

    @Override
    public AuctionResponse createAuction(AuctionRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Auction request cannot be null");
        }

        if (request.getStartTime() == null ||
                request.getEndTime() == null) {

            throw new IllegalArgumentException(
                    "Start time and end time are required");
        }

        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new IllegalArgumentException(
                    "End time must be after start time");
        }

        Auction auction = new Auction();

        auction.setTitle(request.getTitle());
        auction.setDescription(request.getDescription());
        auction.setStartingBid(request.getStartingBid());
        auction.setCurrentBid(request.getStartingBid());
        auction.setStartTime(request.getStartTime());
        auction.setEndTime(request.getEndTime());
        auction.setStatus("CREATED");
        auction.setHighestBidderId(null);
        auction.setWinnerId(null);

        Auction savedAuction = auctionRepository.save(auction);

        return convertToResponse(savedAuction);
    }

    @Override
    public AuctionResponse getAuctionById(Long id) {

        Auction auction = getAuction(id);

        return convertToResponse(auction);
    }

    @Override
    public List<AuctionResponse> getAllAuctions() {

        return auctionRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public List<AuctionResponse> getAuctionsByStatus(String status) {

        return auctionRepository
                .findByStatus(status.toUpperCase())
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public AuctionResponse updateAuction(
            Long id,
            AuctionRequest request) {

        Auction auction = getAuction(id);

        if ("ACTIVE".equalsIgnoreCase(auction.getStatus())) {
            throw new IllegalArgumentException(
                    "Active auction cannot be updated");
        }

        if ("CLOSED".equalsIgnoreCase(auction.getStatus())) {
            throw new IllegalArgumentException(
                    "Closed auction cannot be updated");
        }

        if (request.getStartTime() == null ||
                request.getEndTime() == null) {

            throw new IllegalArgumentException(
                    "Start time and end time are required");
        }

        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new IllegalArgumentException(
                    "End time must be after start time");
        }

        auction.setTitle(request.getTitle());
        auction.setDescription(request.getDescription());
        auction.setStartingBid(request.getStartingBid());
        auction.setCurrentBid(request.getStartingBid());
        auction.setStartTime(request.getStartTime());
        auction.setEndTime(request.getEndTime());

        Auction updatedAuction =
                auctionRepository.save(auction);

        return convertToResponse(updatedAuction);
    }

    @Override
    public AuctionResponse startAuction(Long id) {

        Auction auction = getAuction(id);

        if ("ACTIVE".equalsIgnoreCase(auction.getStatus())) {
            throw new IllegalArgumentException(
                    "Auction is already active");
        }

        if ("CLOSED".equalsIgnoreCase(auction.getStatus())) {
            throw new IllegalArgumentException(
                    "Closed auction cannot be started");
        }

        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(auction.getStartTime())) {
            throw new IllegalArgumentException(
                    "Auction start time has not been reached");
        }

        if (!now.isBefore(auction.getEndTime())) {
            throw new IllegalArgumentException(
                    "Auction end time has already passed");
        }

        auction.setStatus("ACTIVE");

        Auction savedAuction =
                auctionRepository.save(auction);

        return convertToResponse(savedAuction);
    }

    @Override
    @Transactional
    public WinnerResponse closeAuction(Long id) {

        Auction auction = getAuction(id);

        if ("CLOSED".equalsIgnoreCase(auction.getStatus())) {
            throw new IllegalArgumentException(
                    "Auction is already closed");
        }

        auction.setStatus("CLOSED");

        Long winnerId = auction.getHighestBidderId();

        auction.setWinnerId(winnerId);

        Auction savedAuction =
                auctionRepository.save(auction);

        if (winnerId != null) {
            try {
                paymentClient.createPayment(
                        savedAuction.getId(),
                        winnerId,
                        savedAuction.getCurrentBid()
                );
            } catch (Exception e) {
                System.err.println("Payment service failed to trigger: " + e.getMessage());
                // Safe logging so auction still closes cleanly
            }
        }

        return new WinnerResponse(
                savedAuction.getId(),
                winnerId,
                savedAuction.getCurrentBid(),
                "CLOSED"
        );
    }

    @Override
    public boolean validateBid(
            Long id,
            BigDecimal bidAmount) {

        Auction auction = getAuction(id);

        if (!"ACTIVE".equalsIgnoreCase(auction.getStatus())) {
            return false;
        }

        if (bidAmount == null) {
            return false;
        }

        if (!LocalDateTime.now().isBefore(auction.getEndTime())) {
            return false;
        }

        if (auction.getCurrentBid() == null) {

            return bidAmount.compareTo(
                    auction.getStartingBid()) > 0;
        }

        return bidAmount.compareTo(
                auction.getCurrentBid()) > 0;
    }

    @Override
    @Transactional
    public void updateHighestBid(
            Long id,
            Long bidderId,
            BigDecimal bidAmount) {

        Auction auction = getAuction(id);

        if (!"ACTIVE".equalsIgnoreCase(auction.getStatus())) {
            throw new IllegalArgumentException(
                    "Auction is not active");
        }

        if (bidAmount == null) {
            throw new IllegalArgumentException(
                    "Bid amount cannot be null");
        }

        if (!LocalDateTime.now().isBefore(auction.getEndTime())) {

            auction.setStatus("CLOSED");
            auctionRepository.save(auction);

            throw new IllegalArgumentException(
                    "Auction has ended");
        }

        if (auction.getCurrentBid() != null &&
                bidAmount.compareTo(
                        auction.getCurrentBid()) <= 0) {

            throw new IllegalArgumentException(
                    "Bid must be higher than current bid");
        }

        auction.setCurrentBid(bidAmount);
        auction.setHighestBidderId(bidderId);

        auctionRepository.save(auction);
    }

    private Auction getAuction(Long id) {

        if (id == null) {
            throw new IllegalArgumentException(
                    "Auction ID cannot be null");
        }

        return auctionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Auction not found with id: " + id
                        ));
    }

    private AuctionResponse convertToResponse(Auction auction) {

        return new AuctionResponse(
                auction.getId(),
                auction.getTitle(),
                auction.getDescription(),
                auction.getStartingBid(),
                auction.getCurrentBid(),
                auction.getHighestBidderId(),
                auction.getStartTime(),
                auction.getEndTime(),
                auction.getStatus(),
                auction.getWinnerId()
        );
    }
}