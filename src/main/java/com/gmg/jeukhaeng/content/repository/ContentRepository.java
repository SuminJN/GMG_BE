package com.gmg.jeukhaeng.content.repository;

import com.gmg.jeukhaeng.content.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, String> {
    boolean existsByContentId(String contentId);
}
