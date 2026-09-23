package com.klef.ms.sdp.repository;

import com.klef.ms.sdp.entity.Auction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuctionRepository
        extends JpaRepository<Auction, Long> {

    List<Auction> findByStatus(String status);
}