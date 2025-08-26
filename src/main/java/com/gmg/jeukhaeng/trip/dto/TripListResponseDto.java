package com.gmg.jeukhaeng.trip.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripListResponseDto {
    private Long tripId;
    private String title;
    private int days;
    private int totalContents; // 전체 컨텐츠 수
    private String createdAt;
    private String updatedAt;
    private Map<Integer, List<String>> dayContents; // 각 day별 여행지 이름들
}
