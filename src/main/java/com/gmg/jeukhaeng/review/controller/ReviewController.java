package com.gmg.jeukhaeng.review.controller;

import com.gmg.jeukhaeng.review.dto.ReviewDtos.*;
import com.gmg.jeukhaeng.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;


    @PostMapping("/trips/{tripId}/reviews")
    @Operation(summary = "내 다녀온 여행 상세 화면에서 리뷰 작성", description = "내 다녀온 여행 상세 화면에서: 특정 trip 내 콘텐츠 리뷰 작성/수정")
    public ResponseEntity<ReviewResponse> upsertReview(
            @PathVariable Long tripId,
            @RequestBody CreateOrUpdateRequest req
    ) {
        return ResponseEntity.ok(reviewService.upsertMyReview(tripId, req));
    }

    // 내 다녀온 여행 상세 화면에서: 내가 남긴 리뷰 목록
    @GetMapping("/trips/{tripId}/my-reviews")
    @Operation(summary = "내 다녀온 여행 상세 화면에서: 내가 남긴 리뷰 목록", description = " 내가 남긴 리뷰 목록 보기")
    public ResponseEntity<List<ReviewResponse>> myReviewsInTrip(@PathVariable Long tripId) {
        return ResponseEntity.ok(reviewService.getMyReviewsForCompletedTrip(tripId));
    }

    // 콘텐츠 상세(공개): 해당 콘텐츠의 리뷰 + 요약(평균/개수)
    @GetMapping("/contents/{contentId}/reviews")
    @Operation(summary = "해당 콘텐츠의 리뷰 + 별점 보기", description = "contentID로 조회하여 콘텐츠의 리뷰들 및 별점 보기")

    public ResponseEntity<ContentReviewsResponse> contentReviews(
            @PathVariable String contentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(reviewService.getContentReviews(contentId, pageable));
    }
}
