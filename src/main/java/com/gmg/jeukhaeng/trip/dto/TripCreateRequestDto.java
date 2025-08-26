package com.gmg.jeukhaeng.trip.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TripCreateRequestDto {
    private String title;
    private int days;
    
    @JsonProperty("tripContent")
    private List<DayContentDto> tripContent;

    @Data
    public static class DayContentDto {
        private int day;
        private List<Long> contentIds;
    }
}
