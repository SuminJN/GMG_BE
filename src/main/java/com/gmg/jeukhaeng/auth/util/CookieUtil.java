package com.gmg.jeukhaeng.auth.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * JWT 쿠키 설정을 담당하는 유틸리티 클래스
 */
@Component
public class CookieUtil {

    @Value("${jwt.cookie.name}")
    private String cookieName;

    @Value("${jwt.cookie.max-age}")
    private int maxAge;

    @Value("${jwt.cookie.path}")
    private String path;

    @Value("${jwt.cookie.domain}")
    private String domain;

    @Value("${jwt.cookie.secure}")
    private boolean secure;

    @Value("${jwt.cookie.http-only}")
    private boolean httpOnly;

    @Value("${jwt.cookie.same-site}")
    private String sameSite;

    /**
     * JWT 토큰을 HttpOnly 쿠키로 설정합니다.
     * 
     * @param response HTTP 응답 객체
     * @param token JWT 토큰
     */
    public void setJwtCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(cookieName, token);
        cookie.setMaxAge(maxAge);
        cookie.setPath(path);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(secure);
        
        // 도메인 설정 (빈 문자열이 아닌 경우에만)
        if (domain != null && !domain.trim().isEmpty()) {
            cookie.setDomain(domain);
        }
        
        // SameSite 설정 (Set-Cookie 헤더에 직접 추가)
        String sameSiteValue = "SameSite=" + sameSite.toUpperCase();
        response.addHeader("Set-Cookie", 
            cookieName + "=" + token + 
            "; Max-Age=" + maxAge + 
            "; Path=" + path + 
            "; HttpOnly" + 
            (secure ? "; Secure" : "") + 
            "; " + sameSiteValue
        );
    }

    /**
     * JWT 쿠키를 제거합니다.
     * 
     * @param response HTTP 응답 객체
     */
    public void removeJwtCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        cookie.setPath(path);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(secure);
        
        if (domain != null && !domain.trim().isEmpty()) {
            cookie.setDomain(domain);
        }
        
        response.addCookie(cookie);
    }

    /**
     * 쿠키 이름을 반환합니다.
     * 
     * @return 쿠키 이름
     */
    public String getCookieName() {
        return cookieName;
    }
} 