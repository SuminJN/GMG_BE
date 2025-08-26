package com.gmg.jeukhaeng.trip.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripResponseDto {
    private Long tripId;
    private String title;
    private int days;
    private UserDto user;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    
    private List<DayContentDto> tripContents;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDto {
        private Long userId;
        private String email;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DayContentDto {
        private int day;
        private List<TripContentDto> contents;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TripContentDto {
        private Long tripContentId;
        private int sequence;
        private ContentDto content;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContentDto {
        private String contentId;
        private String title;
        private String addr;
        private String tel;
        private String zipcode;
        private String firstImage;
        private String firstImage2;
        private String contentTypeId;
        private String areaCode;
        private String sigunguCode;
        private String mapX;
        private String mapY;
    }
}
