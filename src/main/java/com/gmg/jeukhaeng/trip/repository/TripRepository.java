package com.gmg.jeukhaeng.trip.repository;

import com.gmg.jeukhaeng.trip.entity.Trip;
import com.gmg.jeukhaeng.trip.entity.TripStatus;
import com.gmg.jeukhaeng.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByUserOrderByCreatedAtDesc(User user);
    List<Trip> findByUserAndStatusOrderByCreatedAtDesc(User user, TripStatus status);

    Optional<Trip> findByTripIdAndUserAndStatus(Long tripId, User user, TripStatus status);
    Optional<Trip> findByTripIdAndUser(Long tripId, User user);
}
