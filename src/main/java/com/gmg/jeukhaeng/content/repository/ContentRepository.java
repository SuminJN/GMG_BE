package com.gmg.jeukhaeng.content.repository;

import com.gmg.jeukhaeng.content.entity.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, String> {
    boolean existsByContentId(String contentId);
    Page<Content> findByAreaCodeAndSigunguCode(String areaCode, String sigunguCode, Pageable pageable);
}
