package com.gmg.jeukhaeng.review.dto;

import lombok.*;

import java.time.LocalDateTime;

public class ReviewDtos {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CreateOrUpdateRequest {
        private String contentId; // 리뷰 대상 콘텐츠
        private Integer score;    // 1~5
        private String comment;   // 선택
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ReviewResponse {
        private Long reviewId;
        private String contentId;
        private String contentTitle;
        private Integer score;
        private String comment;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ContentReviewsResponse {
        private Double averageScore; // null일 수 있음
        private long reviewCount;
        private java.util.List<ReviewResponse> reviews;
    }
}
