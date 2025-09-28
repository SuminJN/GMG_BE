package com.gmg.jeukhaeng.trip.repository;

import com.gmg.jeukhaeng.trip.entity.Trip;
import com.gmg.jeukhaeng.trip.entity.TripStatus;
import com.gmg.jeukhaeng.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {

    Optional<Trip> findByTripIdAndUserAndStatus(Long tripId, User user, TripStatus status);
    Optional<Trip> findByTripIdAndUser(Long tripId, User user);

    // Fetch Join 최적화 메서드들
    @Query("select distinct t from Trip t\n" +
            " left join fetch t.tripContents tc\n" +
            " left join fetch tc.content c\n" +
            " where t.user = :user\n" +
            " order by t.createdAt desc")
    List<Trip> findByUserOrderByCreatedAtDescWithContents(@Param("user") User user);

    @Query("select distinct t from Trip t\n" +
            " left join fetch t.tripContents tc\n" +
            " left join fetch tc.content c\n" +
            " where t.user = :user and t.status = :status\n" +
            " order by t.createdAt desc")
    List<Trip> findByUserAndStatusOrderByCreatedAtDescWithContents(@Param("user") User user, @Param("status") TripStatus status);

    @Query("select distinct t from Trip t\n" +
            " left join fetch t.tripContents tc\n" +
            " left join fetch tc.content c\n" +
            " where t.tripId = :tripId and t.user = :user")
    Optional<Trip> findByTripIdAndUserWithContents(@Param("tripId") Long tripId, @Param("user") User user);

    @Query("select distinct t from Trip t\n" +
            " left join fetch t.tripContents tc\n" +
            " left join fetch tc.content c\n" +
            " where t.tripId = :tripId and t.user = :user and t.status = :status")
    Optional<Trip> findByTripIdAndUserAndStatusWithContents(@Param("tripId") Long tripId, @Param("user") User user, @Param("status") TripStatus status);
}
