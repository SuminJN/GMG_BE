package com.gmg.jeukhaeng.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * 액세스 토큰 생성
     * @param userEmail 사용자 이메일
     * @return 액세스 토큰
     */
    public String generateAccessToken(String userEmail) {
        return generateToken(userEmail, accessTokenExpirationMs, "ACCESS");
    }

    /**
     * 리프레시 토큰 생성
     * @param userEmail 사용자 이메일
     * @return 리프레시 토큰
     */
    public String generateRefreshToken(String userEmail) {
        return generateToken(userEmail, refreshTokenExpirationMs, "REFRESH");
    }

    /**
     * 기존 호환성을 위한 메서드 (액세스 토큰으로 동작)
     * @param userEmail 사용자 이메일
     * @return 액세스 토큰
     */
    @Deprecated
    public String generateToken(String userEmail) {
        return generateAccessToken(userEmail);
    }

    /**
     * 토큰 생성 공통 메서드
     * @param userEmail 사용자 이메일
     * @param expirationMs 만료 시간 (밀리초)
     * @param tokenType 토큰 타입
     * @return JWT 토큰
     */
    private String generateToken(String userEmail, long expirationMs, String tokenType) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(userEmail)
                .claim("tokenType", tokenType)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }
    
    /**
     * JWT 토큰을 검증하고 사용자 이메일을 반환합니다.
     * 
     * @param token JWT 토큰
     * @return 사용자 이메일 (토큰이 유효하지 않으면 null)
     */
    public String validateTokenAndGetEmail(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            // 토큰 만료 확인
            if (claims.getExpiration().before(new Date())) {
                return null;
            }
            
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 액세스 토큰 검증 및 사용자 이메일 반환
     * @param token 액세스 토큰
     * @return 사용자 이메일 (유효하지 않으면 null)
     */
    public String validateAccessTokenAndGetEmail(String token) {
        return validateTokenAndGetEmail(token, "ACCESS");
    }

    /**
     * 리프레시 토큰 검증 및 사용자 이메일 반환
     * @param token 리프레시 토큰
     * @return 사용자 이메일 (유효하지 않으면 null)
     */
    public String validateRefreshTokenAndGetEmail(String token) {
        return validateTokenAndGetEmail(token, "REFRESH");
    }

    /**
     * 특정 타입의 JWT 토큰을 검증하고 사용자 이메일을 반환합니다.
     * 
     * @param token JWT 토큰
     * @param expectedTokenType 예상되는 토큰 타입
     * @return 사용자 이메일 (토큰이 유효하지 않으면 null)
     */
    private String validateTokenAndGetEmail(String token, String expectedTokenType) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            // 토큰 만료 확인
            if (claims.getExpiration().before(new Date())) {
                return null;
            }

            // 토큰 타입 확인
            String tokenType = claims.get("tokenType", String.class);
            if (!expectedTokenType.equals(tokenType)) {
                return null;
            }
            
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * JWT 토큰이 유효한지 확인합니다.
     * 
     * @param token JWT 토큰
     * @return 유효하면 true, 아니면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 액세스 토큰이 유효한지 확인합니다.
     * @param token 액세스 토큰
     * @return 유효하면 true, 아니면 false
     */
    public boolean validateAccessToken(String token) {
        return validateTokenAndGetEmail(token, "ACCESS") != null;
    }

    /**
     * 리프레시 토큰이 유효한지 확인합니다.
     * @param token 리프레시 토큰
     * @return 유효하면 true, 아니면 false
     */
    public boolean validateRefreshToken(String token) {
        return validateTokenAndGetEmail(token, "REFRESH") != null;
    }

    /**
     * 리프레시 토큰 만료 시간을 반환합니다.
     * @return 리프레시 토큰 만료 시간 (밀리초)
     */
    public long getRefreshTokenExpirationMs() {
        return refreshTokenExpirationMs;
    }
}
