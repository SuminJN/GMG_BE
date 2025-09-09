package com.gmg.jeukhaeng.auth.scheduler;

import com.gmg.jeukhaeng.auth.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenCleanupScheduler {

    private final RefreshTokenService refreshTokenService;

    /**
     * 매일 새벽 2시에 만료된 리프레시 토큰들을 정리합니다.
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredTokens() {
        log.info("만료된 리프레시 토큰 정리 작업을 시작합니다");
        refreshTokenService.cleanupExpiredTokens();
    }
}
