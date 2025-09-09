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
    private final long testAccessTokenExpirationMs = 3600000; // 1시간
    private final long testRefreshTokenExpirationMs = 604800000; // 7일

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secretKey", testSecretKey);
        ReflectionTestUtils.setField(jwtUtil, "accessTokenExpirationMs", testAccessTokenExpirationMs);
        ReflectionTestUtils.setField(jwtUtil, "refreshTokenExpirationMs", testRefreshTokenExpirationMs);
    }

    @Test
    @DisplayName("유효한 사용자 이메일로 액세스 토큰 생성 성공")
    void shouldGenerateAccessTokenWithValidUserEmail() {
        // given
        String userEmail = "test@example.com";

        // when
        String token = jwtUtil.generateAccessToken(userEmail);

        // then
        assertThat(token).isNotNull();
        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("유효한 사용자 이메일로 리프레시 토큰 생성 성공")
    void shouldGenerateRefreshTokenWithValidUserEmail() {
        // given
        String userEmail = "test@example.com";

        // when
        String token = jwtUtil.generateRefreshToken(userEmail);

        // then
        assertThat(token).isNotNull();
        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("생성된 액세스 토큰에서 사용자 이메일 추출 성공")
    void shouldExtractUserEmailFromValidAccessToken() {
        // given
        String userEmail = "test@example.com";
        String token = jwtUtil.generateAccessToken(userEmail);

        // when
        String extractedEmail = jwtUtil.validateAccessTokenAndGetEmail(token);

        // then
        assertThat(extractedEmail).isEqualTo(userEmail);
    }

    @Test
    @DisplayName("생성된 리프레시 토큰에서 사용자 이메일 추출 성공")
    void shouldExtractUserEmailFromValidRefreshToken() {
        // given
        String userEmail = "test@example.com";
        String token = jwtUtil.generateRefreshToken(userEmail);

        // when
        String extractedEmail = jwtUtil.validateRefreshTokenAndGetEmail(token);

        // then
        assertThat(extractedEmail).isEqualTo(userEmail);
    }

    @Test
    @DisplayName("유효한 액세스 토큰 검증 성공")
    void shouldValidateValidAccessToken() {
        // given
        String userEmail = "test@example.com";
        String token = jwtUtil.generateAccessToken(userEmail);

        // when
        boolean isValid = jwtUtil.validateAccessToken(token);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("유효한 리프레시 토큰 검증 성공")
    void shouldValidateValidRefreshToken() {
        // given
        String userEmail = "test@example.com";
        String token = jwtUtil.generateRefreshToken(userEmail);

        // when
        boolean isValid = jwtUtil.validateRefreshToken(token);

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
    @DisplayName("액세스 토큰으로 리프레시 토큰 검증 시도하면 실패")
    void shouldFailToValidateAccessTokenAsRefreshToken() {
        // given
        String userEmail = "test@example.com";
        String accessToken = jwtUtil.generateAccessToken(userEmail);

        // when
        boolean isValid = jwtUtil.validateRefreshToken(accessToken);
        String extractedEmail = jwtUtil.validateRefreshTokenAndGetEmail(accessToken);

        // then
        assertThat(isValid).isFalse();
        assertThat(extractedEmail).isNull();
    }

    @Test
    @DisplayName("리프레시 토큰으로 액세스 토큰 검증 시도하면 실패")
    void shouldFailToValidateRefreshTokenAsAccessToken() {
        // given
        String userEmail = "test@example.com";
        String refreshToken = jwtUtil.generateRefreshToken(userEmail);

        // when
        boolean isValid = jwtUtil.validateAccessToken(refreshToken);
        String extractedEmail = jwtUtil.validateAccessTokenAndGetEmail(refreshToken);

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
    @DisplayName("다른 시크릿 키로 서명된 액세스 토큰 검증 실패")
    void shouldFailToValidateAccessTokenSignedWithDifferentSecret() {
        // given
        JwtUtil anotherJwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(anotherJwtUtil, "secretKey", "differentSecretKey123456789012345678901234567890");
        ReflectionTestUtils.setField(anotherJwtUtil, "accessTokenExpirationMs", testAccessTokenExpirationMs);
        ReflectionTestUtils.setField(anotherJwtUtil, "refreshTokenExpirationMs", testRefreshTokenExpirationMs);
        
        String userEmail = "test@example.com";
        String tokenSignedWithDifferentKey = anotherJwtUtil.generateAccessToken(userEmail);

        // when
        boolean isValid = jwtUtil.validateAccessToken(tokenSignedWithDifferentKey);
        String extractedEmail = jwtUtil.validateAccessTokenAndGetEmail(tokenSignedWithDifferentKey);

        // then
        assertThat(isValid).isFalse();
        assertThat(extractedEmail).isNull();
    }

    @Test
    @DisplayName("만료된 액세스 토큰 검증 실패")
    void shouldFailToValidateExpiredAccessToken() {
        // given - 즉시 만료되는 토큰 생성
        JwtUtil expiredJwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(expiredJwtUtil, "secretKey", testSecretKey);
        ReflectionTestUtils.setField(expiredJwtUtil, "accessTokenExpirationMs", -1L); // 이미 만료된 토큰
        ReflectionTestUtils.setField(expiredJwtUtil, "refreshTokenExpirationMs", testRefreshTokenExpirationMs);
        
        String userEmail = "test@example.com";
        String expiredToken = expiredJwtUtil.generateAccessToken(userEmail);

        // when
        boolean isValid = jwtUtil.validateAccessToken(expiredToken);
        String extractedEmail = jwtUtil.validateAccessTokenAndGetEmail(expiredToken);

        // then
        assertThat(isValid).isFalse();
        assertThat(extractedEmail).isNull();
    }

    @Test
    @DisplayName("서로 다른 사용자 이메일로 생성된 액세스 토큰들은 각각 올바른 이메일 반환")
    void shouldReturnCorrectEmailForDifferentUsersAccessToken() {
        // given
        String userEmail1 = "user1@example.com";
        String userEmail2 = "user2@example.com";
        
        String token1 = jwtUtil.generateAccessToken(userEmail1);
        String token2 = jwtUtil.generateAccessToken(userEmail2);

        // when
        String extractedEmail1 = jwtUtil.validateAccessTokenAndGetEmail(token1);
        String extractedEmail2 = jwtUtil.validateAccessTokenAndGetEmail(token2);

        // then
        assertThat(extractedEmail1).isEqualTo(userEmail1);
        assertThat(extractedEmail2).isEqualTo(userEmail2);
        assertThat(extractedEmail1).isNotEqualTo(extractedEmail2);
    }

    @Test
    @DisplayName("특수 문자가 포함된 이메일로 액세스 토큰 생성 및 검증 성공")
    void shouldHandleEmailsWithSpecialCharactersForAccessToken() {
        // given
        String userEmail = "test.user+label@example-domain.co.kr";

        // when
        String token = jwtUtil.generateAccessToken(userEmail);
        String extractedEmail = jwtUtil.validateAccessTokenAndGetEmail(token);

        // then
        assertThat(extractedEmail).isEqualTo(userEmail);
        assertThat(jwtUtil.validateAccessToken(token)).isTrue();
    }

    @Test
    @DisplayName("특수 문자가 포함된 이메일로 리프레시 토큰 생성 및 검증 성공")
    void shouldHandleEmailsWithSpecialCharactersForRefreshToken() {
        // given
        String userEmail = "test.user+label@example-domain.co.kr";

        // when
        String token = jwtUtil.generateRefreshToken(userEmail);
        String extractedEmail = jwtUtil.validateRefreshTokenAndGetEmail(token);

        // then
        assertThat(extractedEmail).isEqualTo(userEmail);
        assertThat(jwtUtil.validateRefreshToken(token)).isTrue();
    }
}
