package com.gmg.jeukhaeng.auth.controller;

import com.gmg.jeukhaeng.auth.dto.TokenResponseDto;
import com.gmg.jeukhaeng.auth.service.RefreshTokenService;
import com.gmg.jeukhaeng.user.dto.MyPageResponseDto;
import com.gmg.jeukhaeng.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 인증 관련 API 엔드포인트
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    /**
     * 현재 인증된 사용자 정보 조회
     * 
     * @return 사용자 정보
     */
    @GetMapping("/me")
    @Operation(summary = "마이페이지 조회", description = "마이페이지에서 내 정보가 표시됩니다.")
    public ResponseEntity<MyPageResponseDto> me(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(userService.getMyPageByEmail(email));
    }

    /**
     * 리프레시 토큰으로 새로운 액세스 토큰 발급
     * 
     * @param refreshToken 리프레시 토큰
     * @return 새로운 액세스 토큰과 기존 리프레시 토큰
     */
    @PostMapping("/refresh")
    @Operation(summary = "토큰 갱신", description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급받습니다.")
    public ResponseEntity<TokenResponseDto> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            TokenResponseDto tokenResponse = refreshTokenService.refreshAccessToken(refreshToken);
            return ResponseEntity.ok(tokenResponse);
        } catch (IllegalArgumentException e) {
            log.warn("토큰 갱신 실패: {}", e.getMessage());
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * 로그아웃 - 리프레시 토큰 삭제
     * 
     * @param authentication 인증 정보
     * @return 로그아웃 결과
     */
    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "현재 사용자의 리프레시 토큰을 삭제하여 로그아웃합니다.")
    public ResponseEntity<Map<String, String>> logout(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.badRequest().build();
        }

        String email = authentication.getName();
        refreshTokenService.deleteRefreshToken(email);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "로그아웃되었습니다");
        return ResponseEntity.ok(response);
    }
} 