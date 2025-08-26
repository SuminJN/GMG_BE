package com.gmg.jeukhaeng.trip.entity;

import com.gmg.jeukhaeng.content.entity.Content;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "trip_contents")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tripContentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    private int day;

    private int sequence;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void assignTrip(Trip trip) {
        this.trip = trip;
    }
}