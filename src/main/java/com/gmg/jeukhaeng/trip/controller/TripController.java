package com.gmg.jeukhaeng.trip.controller;

import com.gmg.jeukhaeng.trip.dto.TripCreateRequestDto;
import com.gmg.jeukhaeng.trip.dto.TripCreateResponseDto;
import com.gmg.jeukhaeng.trip.service.TripService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
