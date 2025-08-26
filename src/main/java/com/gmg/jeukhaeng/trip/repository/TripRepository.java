package com.gmg.jeukhaeng.trip.repository;

import com.gmg.jeukhaeng.trip.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, Long> {
}
