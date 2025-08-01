package com.gmg.jeukhaeng.auth.controller;

import com.gmg.jeukhaeng.auth.config.OAuthProperties;
import com.gmg.jeukhaeng.auth.service.KakaoOAuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth/kakao")
public class KakaoLoginController {

    private final OAuthProperties oAuthProperties;
    private final KakaoOAuthService kakaoOAuthService;

    @Value("${frontend.redirect-base-url}")
    private String frontendRedirectBaseUrl;

    // 카카오 로그인 시작 (프론트에서 이 주소로 연결)
    @GetMapping("/login")
    public void redirectToKakaoLogin(HttpServletResponse response) throws IOException {
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize"
                + "?client_id=" + oAuthProperties.getClientId()
                + "&redirect_uri=" + oAuthProperties.getRedirectUri()
                + "&response_type=code";

        response.sendRedirect(kakaoAuthUrl);
    }

    // 카카오 로그인 콜백 처리
    @GetMapping("/callback")
    public void kakaoCallback(@RequestParam String code, HttpServletResponse response) throws IOException {
        String jwt = kakaoOAuthService.processKakaoLogin(code);

        // JWT 토큰을 클라이언트로 전달
        String redirectUri = frontendRedirectBaseUrl + "/login/success?token=" + jwt;
        response.sendRedirect(redirectUri);
    }
}