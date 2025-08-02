package com.gmg.jeukhaeng.auth.controller;

import com.gmg.jeukhaeng.auth.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
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

    private final CookieUtil cookieUtil;

    /**
     * 현재 인증된 사용자 정보 조회
     * 
     * @return 사용자 정보
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", authentication != null && authentication.isAuthenticated());
        response.put("principal", authentication != null ? authentication.getPrincipal() : null);
        response.put("authorities", authentication != null ? authentication.getAuthorities() : null);
        
        log.info("현재 사용자 정보 조회: {}", response);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 로그아웃 (쿠키 제거)
     * 
     * @param response HTTP 응답 객체
     * @return 로그아웃 성공 메시지
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletResponse response) {
        // JWT 쿠키 제거
        cookieUtil.removeJwtCookie(response);
        
        // SecurityContext 클리어
        SecurityContextHolder.clearContext();
        
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "로그아웃이 완료되었습니다.");
        
        log.info("사용자 로그아웃 완료");
        
        return ResponseEntity.ok(responseBody);
    }
} 