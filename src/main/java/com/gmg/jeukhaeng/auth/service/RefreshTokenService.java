package com.gmg.jeukhaeng.auth.service;

import com.gmg.jeukhaeng.auth.dto.TokenResponseDto;
import com.gmg.jeukhaeng.auth.entity.RefreshToken;
import com.gmg.jeukhaeng.auth.repository.RefreshTokenRepository;
import com.gmg.jeukhaeng.auth.util.JwtUtil;
import com.gmg.jeukhaeng.user.entity.User;
import com.gmg.jeukhaeng.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpirationMs;

    /**
     * 사용자를 위한 토큰 쌍 생성
     * @param userEmail 사용자 이메일
     * @return 액세스 토큰과 리프레시 토큰을 포함한 응답
     */
    @Transactional
    public TokenResponseDto generateTokens(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userEmail));

        // 액세스 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(userEmail);
        
        // 리프레시 토큰 생성
        String refreshTokenValue = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusNanos(jwtUtil.getRefreshTokenExpirationMs() * 1_000_000);

        // 기존 리프레시 토큰이 있다면 업데이트, 없다면 새로 생성
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUser(user);
        if (existingToken.isPresent()) {
            existingToken.get().updateToken(refreshTokenValue, expiresAt);
        } else {
            RefreshToken refreshToken = RefreshToken.builder()
                    .token(refreshTokenValue)
                    .user(user)
                    .expiresAt(expiresAt)
                    .build();
            refreshTokenRepository.save(refreshToken);
        }

        return TokenResponseDto.of(accessToken, refreshTokenValue, accessTokenExpirationMs / 1000);
    }

    /**
     * 리프레시 토큰으로 새로운 액세스 토큰 발급
     * @param refreshTokenValue 리프레시 토큰 값
     * @return 새로운 액세스 토큰
     */
    @Transactional
    public TokenResponseDto refreshAccessToken(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다"));

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new IllegalArgumentException("만료된 리프레시 토큰입니다");
        }

        String userEmail = refreshToken.getUser().getEmail();
        String newAccessToken = jwtUtil.generateAccessToken(userEmail);

        return TokenResponseDto.of(newAccessToken, refreshTokenValue, accessTokenExpirationMs / 1000);
    }

    /**
     * 리프레시 토큰 삭제 (로그아웃)
     * @param userEmail 사용자 이메일
     */
    @Transactional
    public void deleteRefreshToken(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userEmail));
        
        refreshTokenRepository.deleteByUser(user);
    }

    /**
     * 만료된 리프레시 토큰들 정리
     */
    @Transactional
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
        log.info("만료된 리프레시 토큰들을 정리했습니다");
    }

    /**
     * 리프레시 토큰 유효성 검증
     * @param refreshTokenValue 리프레시 토큰 값
     * @return 유효하면 true
     */
    @Transactional(readOnly = true)
    public boolean validateRefreshToken(String refreshTokenValue) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByToken(refreshTokenValue);
        return refreshToken.isPresent() && !refreshToken.get().isExpired();
    }
}
