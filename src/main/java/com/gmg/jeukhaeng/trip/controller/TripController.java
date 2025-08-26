package com.gmg.jeukhaeng.trip.controller;

import com.gmg.jeukhaeng.trip.dto.TripCreateRequestDto;
import com.gmg.jeukhaeng.trip.dto.TripCreateResponseDto;
import com.gmg.jeukhaeng.trip.dto.TripResponseDto;
import com.gmg.jeukhaeng.trip.dto.TripListResponseDto;
import com.gmg.jeukhaeng.trip.service.TripService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trips")
@Tag(name = "Trip API", description = "여행 일정 관리 API")
public class TripController {
    
    private final TripService tripService;
    
    @PostMapping
    @Operation(summary = "여행 일정 생성", description = "새로운 여행 일정을 생성합니다.")
    public ResponseEntity<TripCreateResponseDto> createTrip(@RequestBody TripCreateRequestDto requestDto) {
        log.info("여행 일정 생성 요청: {}", requestDto.getTitle());
        
        TripCreateResponseDto response = tripService.createTrip(requestDto);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "여행 일정 목록 조회", description = "사용자의 모든 여행 일정을 조회합니다.")
    public ResponseEntity<List<TripListResponseDto>> getUserTrips() {
        log.info("여행 일정 목록 조회 요청");
        
        List<TripListResponseDto> trips = tripService.getUserTrips();
        
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/{tripId}")
    @Operation(summary = "여행 일정 상세 조회", description = "특정 여행 일정의 상세 정보를 조회합니다.")
    public ResponseEntity<TripResponseDto> getTripDetail(@PathVariable Long tripId) {
        log.info("여행 일정 상세 조회 요청: tripId={}", tripId);
        
        TripResponseDto trip = tripService.getTripDetail(tripId);
        
        return ResponseEntity.ok(trip);
    }

    @GetMapping("/me/planned")
    @Operation(summary = "생성한 여행 일정 전체 조회", description = "생성한 여행 일정의 전체 정보를 조회합니다.")
    public ResponseEntity<List<TripListResponseDto>> myPlannedTrips() {
        return ResponseEntity.ok(tripService.getPlannedTripList());
    }

    @GetMapping("/me/planned/{tripId}")
    @Operation(summary = "생성한 여행 일정 상세 조회", description = "특정 생성한 여행 일정의 상세 정보를 조회합니다.")

    public ResponseEntity<TripResponseDto> myPlannedTripDetail(@PathVariable Long tripId) {
        return ResponseEntity.ok(tripService.getPlannedTripDetail(tripId));
    }

    // 다녀온 목록/상세
    @GetMapping("/me/completed")
    @Operation(summary = "다녀온 여행 전체 조회", description = "다녀온 여행 일정의 전체 정보를 조회합니다.")
    public ResponseEntity<List<TripListResponseDto>> myCompletedTrips() {
        return ResponseEntity.ok(tripService.getCompletedTripList());
    }

    @GetMapping("/me/completed/{tripId}")
    @Operation(summary = "다녀온 여행 상세 조회", description = "다녀온 여행 일정의 상세 정보를 조회합니다.")
    public ResponseEntity<TripResponseDto> myCompletedTripDetail(@PathVariable Long tripId) {
        return ResponseEntity.ok(tripService.getCompletedTripDetail(tripId));
    }


    // “다녀왔어요” 버튼
    @PatchMapping("/{tripId}/complete")
    @Operation(summary = "다녀왔어요 버튼", description = "여행 일정의 상태를 다녀옴으로 바꿈.")
    public ResponseEntity<Void> complete(@PathVariable Long tripId) {
        tripService.completeTrip(tripId);
        return ResponseEntity.noContent().build();
    }
}
