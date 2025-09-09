package com.gmg.jeukhaeng.auth.repository;

import com.gmg.jeukhaeng.auth.entity.RefreshToken;
import com.gmg.jeukhaeng.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * 토큰 값으로 리프레시 토큰 조회
     * @param token 토큰 값
     * @return RefreshToken 엔티티
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * 사용자로 리프레시 토큰 조회
     * @param user 사용자
     * @return RefreshToken 엔티티
     */
    Optional<RefreshToken> findByUser(User user);

    /**
     * 사용자의 기존 리프레시 토큰 삭제
     * @param user 사용자
     */
    void deleteByUser(User user);

    /**
     * 만료된 토큰들 삭제
     * @param now 현재 시간
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);
}
