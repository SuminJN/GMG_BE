package com.gmg.jeukhaeng.auth.controller;

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
} 