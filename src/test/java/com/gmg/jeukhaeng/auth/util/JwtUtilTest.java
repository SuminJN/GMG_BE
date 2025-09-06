package com.gmg.jeukhaeng.auth.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JWT 유틸리티 테스트")
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String testSecretKey = "testSecretKeyForJwtTokenGenerationAndValidation123456789";
    private final long testExpirationMs = 3600000; // 1시간

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secretKey", testSecretKey);
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", testExpirationMs);
    }

    @Test
    @DisplayName("유효한 사용자 이메일로 JWT 토큰 생성 성공")
    void shouldGenerateTokenWithValidUserEmail() {
        // given
        String userEmail = "test@example.com";

        // when
        String token = jwtUtil.generateToken(userEmail);

        // then
        assertThat(token).isNotNull();
        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3); // JWT는 header.payload.signature 형태
    }

    @Test
    @DisplayName("생성된 JWT 토큰에서 사용자 이메일 추출 성공")
    void shouldExtractUserEmailFromValidToken() {
        // given
        String userEmail = "test@example.com";
        String token = jwtUtil.generateToken(userEmail);

        // when
        String extractedEmail = jwtUtil.validateTokenAndGetEmail(token);

        // then
        assertThat(extractedEmail).isEqualTo(userEmail);
    }

    @Test
    @DisplayName("유효한 JWT 토큰 검증 성공")
    void shouldValidateValidToken() {
        // given
        String userEmail = "test@example.com";
        String token = jwtUtil.generateToken(userEmail);

        // when
        boolean isValid = jwtUtil.validateToken(token);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("잘못된 형식의 JWT 토큰 검증 실패")
    void shouldFailToValidateInvalidFormatToken() {
        // given
        String invalidToken = "invalid.token.format";

        // when
        boolean isValid = jwtUtil.validateToken(invalidToken);
        String extractedEmail = jwtUtil.validateTokenAndGetEmail(invalidToken);

        // then
        assertThat(isValid).isFalse();
        assertThat(extractedEmail).isNull();
    }

    @Test
    @DisplayName("null 토큰 검증 실패")
    void shouldFailToValidateNullToken() {
        // given
        String nullToken = null;

        // when
        boolean isValid = jwtUtil.validateToken(nullToken);
        String extractedEmail = jwtUtil.validateTokenAndGetEmail(nullToken);

        // then
        assertThat(isValid).isFalse();
        assertThat(extractedEmail).isNull();
    }

    @Test
    @DisplayName("빈 문자열 토큰 검증 실패")
    void shouldFailToValidateEmptyToken() {
        // given
        String emptyToken = "";

        // when
        boolean isValid = jwtUtil.validateToken(emptyToken);
        String extractedEmail = jwtUtil.validateTokenAndGetEmail(emptyToken);

        // then
        assertThat(isValid).isFalse();
        assertThat(extractedEmail).isNull();
    }

    @Test
    @DisplayName("공백만 있는 토큰 검증 실패")
    void shouldFailToValidateWhitespaceToken() {
        // given
        String whitespaceToken = "   ";

        // when
        boolean isValid = jwtUtil.validateToken(whitespaceToken);
        String extractedEmail = jwtUtil.validateTokenAndGetEmail(whitespaceToken);

        // then
        assertThat(isValid).isFalse();
        assertThat(extractedEmail).isNull();
    }

    @Test
    @DisplayName("다른 시크릿 키로 서명된 토큰 검증 실패")
    void shouldFailToValidateTokenSignedWithDifferentSecret() {
        // given
        JwtUtil anotherJwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(anotherJwtUtil, "secretKey", "differentSecretKey123456789012345678901234567890");
        ReflectionTestUtils.setField(anotherJwtUtil, "expirationMs", testExpirationMs);
        
        String userEmail = "test@example.com";
        String tokenSignedWithDifferentKey = anotherJwtUtil.generateToken(userEmail);

        // when
        boolean isValid = jwtUtil.validateToken(tokenSignedWithDifferentKey);
        String extractedEmail = jwtUtil.validateTokenAndGetEmail(tokenSignedWithDifferentKey);

        // then
        assertThat(isValid).isFalse();
        assertThat(extractedEmail).isNull();
    }

    @Test
    @DisplayName("만료된 JWT 토큰 검증 실패")
    void shouldFailToValidateExpiredToken() {
        // given - 즉시 만료되는 토큰 생성
        JwtUtil expiredJwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(expiredJwtUtil, "secretKey", testSecretKey);
        ReflectionTestUtils.setField(expiredJwtUtil, "expirationMs", -1L); // 이미 만료된 토큰
        
        String userEmail = "test@example.com";
        String expiredToken = expiredJwtUtil.generateToken(userEmail);

        // when
        boolean isValid = jwtUtil.validateToken(expiredToken);
        String extractedEmail = jwtUtil.validateTokenAndGetEmail(expiredToken);

        // then
        assertThat(isValid).isFalse();
        assertThat(extractedEmail).isNull();
    }

    @Test
    @DisplayName("서로 다른 사용자 이메일로 생성된 토큰들은 각각 올바른 이메일 반환")
    void shouldReturnCorrectEmailForDifferentUsers() {
        // given
        String userEmail1 = "user1@example.com";
        String userEmail2 = "user2@example.com";
        
        String token1 = jwtUtil.generateToken(userEmail1);
        String token2 = jwtUtil.generateToken(userEmail2);

        // when
        String extractedEmail1 = jwtUtil.validateTokenAndGetEmail(token1);
        String extractedEmail2 = jwtUtil.validateTokenAndGetEmail(token2);

        // then
        assertThat(extractedEmail1).isEqualTo(userEmail1);
        assertThat(extractedEmail2).isEqualTo(userEmail2);
        assertThat(extractedEmail1).isNotEqualTo(extractedEmail2);
    }

    @Test
    @DisplayName("특수 문자가 포함된 이메일로 토큰 생성 및 검증 성공")
    void shouldHandleEmailsWithSpecialCharacters() {
        // given
        String userEmail = "test.user+label@example-domain.co.kr";

        // when
        String token = jwtUtil.generateToken(userEmail);
        String extractedEmail = jwtUtil.validateTokenAndGetEmail(token);

        // then
        assertThat(extractedEmail).isEqualTo(userEmail);
        assertThat(jwtUtil.validateToken(token)).isTrue();
    }
}
