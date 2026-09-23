package com.klef.ms.sdp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "auctions")
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal startingBid;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal currentBid;

    private Long highestBidderId;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private String status;

    private Long winnerId;

    // Default constructor required by JPA
    public Auction() {
    }

    // Full constructor
    public Auction(
            Long id,
            String title,
            String description,
            BigDecimal startingBid,
            BigDecimal currentBid,
            Long highestBidderId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            String status,
            Long winnerId) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.startingBid = startingBid;
        this.currentBid = currentBid;
        this.highestBidderId = highestBidderId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.winnerId = winnerId;
    }

    // Automatically assigns default values before saving to DB if fields are null
    @PrePersist
    public void prePersist() {
        if (this.currentBid == null) {
            this.currentBid = this.startingBid;
        }
        if (this.startTime == null) {
            this.startTime = LocalDateTime.now();
        }
        if (this.endTime == null) {
            this.endTime = LocalDateTime.now().plusDays(7); // Default 7 days duration
        }
        if (this.status == null) {
            this.status = "ACTIVE";
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getStartingBid() {
        return startingBid;
    }

    public void setStartingBid(BigDecimal startingBid) {
        this.startingBid = startingBid;
    }

    public BigDecimal getCurrentBid() {
        return currentBid;
    }

    public void setCurrentBid(BigDecimal currentBid) {
        this.currentBid = currentBid;
    }

    public Long getHighestBidderId() {
        return highestBidderId;
    }

    public void setHighestBidderId(Long highestBidderId) {
        this.highestBidderId = highestBidderId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Long winnerId) {
        this.winnerId = winnerId;
    }
}