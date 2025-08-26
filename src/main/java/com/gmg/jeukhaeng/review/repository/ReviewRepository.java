package com.gmg.jeukhaeng.review.repository;

import com.gmg.jeukhaeng.content.entity.Content;
import com.gmg.jeukhaeng.review.entity.Review;
import com.gmg.jeukhaeng.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByUserAndContent(User user, Content content);

    Page<Review> findByContent(Content content, Pageable pageable);

    @Query("select avg(r.score) from Review r where r.content.contentId = :contentId")
    Double getAverageScore(String contentId);

    long countByContent_ContentId(String contentId);
}
