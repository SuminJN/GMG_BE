package com.gmg.jeukhaeng.trip.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripCreateResponseDto {
    private Long tripId;
    private String message;
}
