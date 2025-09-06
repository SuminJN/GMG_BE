package com.gmg.jeukhaeng.auth.handler;

import com.gmg.jeukhaeng.auth.util.JwtUtil;
import com.gmg.jeukhaeng.user.entity.User;
import com.gmg.jeukhaeng.user.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Value("${frontend.redirect-base-url}")
    private String frontendRedirectBaseUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                      HttpServletResponse response, 
                                      Authentication authentication) throws IOException, ServletException {
        
        log.info("OAuth2 로그인 성공: {}", authentication.getName());
        
        try {
            // OAuth2 인증 토큰에서 사용자 정보 추출
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            OAuth2User oauth2User = oauthToken.getPrincipal();
            
            // OAuth 제공자 정보 [kakao, google]
            String provider = oauthToken.getAuthorizedClientRegistrationId();
            
            // OAuth2User에서 사용자 정보 추출
            Map<String, Object> attributes = oauth2User.getAttributes();
            
            String email = extractEmail(attributes, provider);
            String nickname = extractNickname(attributes, provider);
            String providerId = extractProviderId(attributes, provider);
            
            log.info("OAuth2 사용자 정보 - Provider: {}, Email: {}, Nickname: {}, ProviderId: {}", 
                    provider, email, nickname, providerId);
            
            // 사용자 정보로 회원가입 또는 로그인 처리
            User user = userService.findOrCreate(provider, providerId, email, nickname);
            
            // JWT 토큰 생성
            String jwtToken = jwtUtil.generateToken(user.getEmail());

            // 프론트엔드로 리다이렉트 (토큰을 쿼리 파라미터로 전달)
            String encodedToken = URLEncoder.encode(jwtToken, StandardCharsets.UTF_8);
            String redirectUrl = frontendRedirectBaseUrl + "/login/success?token=" + encodedToken;
            
            log.info("프론트엔드로 리다이렉트: {}", redirectUrl);
            
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
            
        } catch (Exception e) {
            log.error("OAuth2 로그인 성공 처리 중 오류 발생", e);
            // 에러 발생 시 에러 페이지로 리다이렉트
            String errorUrl = frontendRedirectBaseUrl + "/login/error?message=" + e.getMessage();
            getRedirectStrategy().sendRedirect(request, response, errorUrl);
        }
    }
    
    /**
     * OAuth 제공자별 이메일 추출
     */
    private String extractEmail(Map<String, Object> attributes, String provider) {
        switch (provider.toLowerCase()) {
            case "kakao":
                @SuppressWarnings("unchecked")
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                return (String) kakaoAccount.get("email");
            case "google":
                return (String) attributes.get("email");
            default:
                throw new IllegalArgumentException("지원하지 않는 OAuth 제공자: " + provider);
        }
    }
    
    /**
     * OAuth 제공자별 닉네임 추출
     */
    private String extractNickname(Map<String, Object> attributes, String provider) {
        switch (provider.toLowerCase()) {
            case "kakao":
                @SuppressWarnings("unchecked")
                Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
                return (String) properties.get("nickname");
            case "google":
                return (String) attributes.get("name");
            default:
                throw new IllegalArgumentException("지원하지 않는 OAuth 제공자: " + provider);
        }
    }
    
    /**
     * OAuth 제공자별 Provider ID 추출
     */
    private String extractProviderId(Map<String, Object> attributes, String provider) {
        switch (provider.toLowerCase()) {
            case "kakao":
                return String.valueOf(attributes.get("id"));
            case "google":
                return (String) attributes.get("sub");
            default:
                throw new IllegalArgumentException("지원하지 않는 OAuth 제공자: " + provider);
        }
    }
}