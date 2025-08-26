package com.gmg.jeukhaeng.review.service;

import com.gmg.jeukhaeng.content.entity.Content;
import com.gmg.jeukhaeng.content.repository.ContentRepository;
import com.gmg.jeukhaeng.review.dto.ReviewDtos.*;
import com.gmg.jeukhaeng.review.entity.Review;
import com.gmg.jeukhaeng.review.repository.ReviewRepository;
import com.gmg.jeukhaeng.trip.entity.Trip;
import com.gmg.jeukhaeng.trip.entity.TripStatus;
import com.gmg.jeukhaeng.trip.repository.TripRepository;
import com.gmg.jeukhaeng.user.entity.User;
import com.gmg.jeukhaeng.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ContentRepository contentRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    /** 다녀온(Completed) Trip 내 콘텐츠에 대한 내 리뷰 작성/수정 (idempotent upsert) */
    @Transactional
    public ReviewResponse upsertMyReview(Long tripId, CreateOrUpdateRequest req) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User me = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));

        // 1) 내 Completed Trip인지 확인
        Trip trip = tripRepository.findByTripIdAndUserAndStatus(tripId, me, TripStatus.COMPLETED)
                .orElseThrow(() -> new IllegalArgumentException("다녀온 여행이 아니거나 권한이 없습니다."));

        // 2) 해당 Trip 안에 이 contentId가 포함되어 있는지 확인
        boolean included = trip.getTripContents().stream()
                .anyMatch(tc -> tc.getContent().getContentId().equals(req.getContentId()));
        if (!included) throw new IllegalArgumentException("해당 여행 일정에 포함되지 않은 콘텐츠입니다.");

        // 3) 점수 검증
        int score = req.getScore() == null ? 0 : req.getScore();
        if (score < 1 || score > 5) throw new IllegalArgumentException("별점은 1~5 사이여야 합니다.");

        // 4) 리뷰 upsert
        Content content = contentRepository.findById(req.getContentId())
                .orElseThrow(() -> new IllegalArgumentException("콘텐츠가 존재하지 않습니다."));

        Review review = reviewRepository.findByUserAndContent(me, content)
                .map(r -> { r.update(score, req.getComment()); return r; })
                .orElse(Review.builder()
                        .user(me)
                        .content(content)
                        .score(score)
                        .comment(req.getComment())
                        .build());

        Review saved = reviewRepository.save(review);

        return ReviewResponse.builder()
                .reviewId(saved.getReviewId())
                .contentId(content.getContentId())
                .contentTitle(content.getTitle())
                .score(saved.getScore())
                .comment(saved.getComment())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }

    /** 내 Completed Trip에 남긴 리뷰 목록 (마이페이지 > 다녀온 여행 상세 탭) */
    @Transactional(readOnly = true)
    public List<ReviewResponse> getMyReviewsForCompletedTrip(Long tripId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User me = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));

        Trip trip = tripRepository.findByTripIdAndUserAndStatus(tripId, me, TripStatus.COMPLETED)
                .orElseThrow(() -> new IllegalArgumentException("다녀온 여행이 아니거나 권한이 없습니다."));

        return trip.getTripContents().stream()
                .map(tc -> tc.getContent())
                .map(c -> reviewRepository.findByUserAndContent(me, c).orElse(null))
                .filter(r -> r != null)
                .map(r -> ReviewResponse.builder()
                        .reviewId(r.getReviewId())
                        .contentId(r.getContent().getContentId())
                        .contentTitle(r.getContent().getTitle())
                        .score(r.getScore())
                        .comment(r.getComment())
                        .createdAt(r.getCreatedAt())
                        .updatedAt(r.getUpdatedAt())
                        .build()
                ).toList();
    }

    /** 특정 콘텐츠의 리뷰(공개 목록) + 요약 */
    @Transactional(readOnly = true)
    public ContentReviewsResponse getContentReviews(String contentId, Pageable pageable) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("콘텐츠가 존재하지 않습니다."));

        Page<Review> page = reviewRepository.findByContent(content, pageable);
        Double avg = reviewRepository.getAverageScore(contentId);
        long cnt = reviewRepository.countByContent_ContentId(contentId);

        List<ReviewResponse> items = page.map(r -> ReviewResponse.builder()
                .reviewId(r.getReviewId())
                .contentId(contentId)
                .contentTitle(content.getTitle())
                .score(r.getScore())
                .comment(r.getComment())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build()).toList();

        return ContentReviewsResponse.builder()
                .averageScore(avg)
                .reviewCount(cnt)
                .reviews(items)
                .build();
    }
}
