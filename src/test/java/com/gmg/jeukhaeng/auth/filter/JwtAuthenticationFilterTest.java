package com.gmg.jeukhaeng.auth.filter;

import com.gmg.jeukhaeng.auth.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JWT 인증 필터 테스트")
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        // SecurityContext 초기화
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("유효한 JWT 토큰이 포함된 Authorization 헤더로 요청 시 인증 성공")
    void shouldAuthenticateWithValidJwtToken() throws ServletException, IOException {
        // given
        String validToken = "valid.jwt.token";
        String userEmail = "test@example.com";
        String authorizationHeader = "Bearer " + validToken;

        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);
        when(jwtUtil.validateTokenAndGetEmail(validToken)).thenReturn(userEmail);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getPrincipal()).isEqualTo(userEmail);
        assertThat(authentication.getAuthorities()).hasSize(1);
        assertThat(authentication.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");

        verify(filterChain).doFilter(request, response);
        verify(jwtUtil).validateTokenAndGetEmail(validToken);
    }

    @Test
    @DisplayName("Authorization 헤더가 없는 요청 시 인증 없이 필터 체인 진행")
    void shouldContinueFilterChainWhenNoAuthorizationHeader() throws ServletException, IOException {
        // given
        when(request.getHeader("Authorization")).thenReturn(null);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();

        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).validateTokenAndGetEmail(any());
    }

    @Test
    @DisplayName("Bearer가 없는 Authorization 헤더로 요청 시 인증 없이 필터 체인 진행")
    void shouldContinueFilterChainWhenAuthorizationHeaderWithoutBearer() throws ServletException, IOException {
        // given
        String authorizationHeader = "Basic dXNlcjpwYXNzd29yZA==";
        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();

        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).validateTokenAndGetEmail(any());
    }

    @Test
    @DisplayName("Bearer만 있고 토큰이 없는 Authorization 헤더로 요청 시 인증 없이 필터 체인 진행")
    void shouldContinueFilterChainWhenAuthorizationHeaderWithBearerOnly() throws ServletException, IOException {
        // given
        String authorizationHeader = "Bearer ";
        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();

        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).validateTokenAndGetEmail(any());
    }

    @Test
    @DisplayName("유효하지 않은 JWT 토큰으로 요청 시 인증 실패하고 SecurityContext 클리어")
    void shouldClearSecurityContextWhenInvalidJwtToken() throws ServletException, IOException {
        // given
        String invalidToken = "invalid.jwt.token";
        String authorizationHeader = "Bearer " + invalidToken;

        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);
        when(jwtUtil.validateTokenAndGetEmail(invalidToken)).thenReturn(null);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();

        verify(filterChain).doFilter(request, response);
        verify(jwtUtil).validateTokenAndGetEmail(invalidToken);
    }

    @Test
    @DisplayName("JWT 토큰 검증 중 예외 발생 시 SecurityContext 클리어하고 필터 체인 진행")
    void shouldClearSecurityContextWhenJwtValidationThrowsException() throws ServletException, IOException {
        // given
        String tokenCausingException = "problematic.jwt.token";
        String authorizationHeader = "Bearer " + tokenCausingException;

        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);
        when(jwtUtil.validateTokenAndGetEmail(tokenCausingException))
                .thenThrow(new RuntimeException("JWT parsing error"));

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();

        verify(filterChain).doFilter(request, response);
        verify(jwtUtil).validateTokenAndGetEmail(tokenCausingException);
    }

    @Test
    @DisplayName("빈 문자열 토큰으로 요청 시 인증 없이 필터 체인 진행")
    void shouldContinueFilterChainWhenEmptyToken() throws ServletException, IOException {
        // given
        String authorizationHeader = "Bearer ";
        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();

        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).validateTokenAndGetEmail(any());
    }

    @Test
    @DisplayName("공백이 포함된 잘못된 형식의 Authorization 헤더로 요청 시 인증 없이 필터 체인 진행")
    void shouldContinueFilterChainWhenMalformedAuthorizationHeader() throws ServletException, IOException {
        // given
        String authorizationHeader = "  Bearer  ";
        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();

        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).validateTokenAndGetEmail(any());
    }

    @Test
    @DisplayName("JWT 검증은 성공하지만 사용자 이메일이 빈 문자열인 경우 인증 실패")
    void shouldNotAuthenticateWhenUserEmailIsEmpty() throws ServletException, IOException {
        // given
        String validToken = "valid.jwt.token";
        String authorizationHeader = "Bearer " + validToken;

        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);
        when(jwtUtil.validateTokenAndGetEmail(validToken)).thenReturn("");

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();

        verify(filterChain).doFilter(request, response);
        verify(jwtUtil).validateTokenAndGetEmail(validToken);
    }

    @Test
    @DisplayName("JWT 검증은 성공하지만 사용자 이메일이 null인 경우 인증 실패")
    void shouldNotAuthenticateWhenUserEmailIsNull() throws ServletException, IOException {
        // given
        String validToken = "valid.jwt.token";
        String authorizationHeader = "Bearer " + validToken;

        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);
        when(jwtUtil.validateTokenAndGetEmail(validToken)).thenReturn(null);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();

        verify(filterChain).doFilter(request, response);
        verify(jwtUtil).validateTokenAndGetEmail(validToken);
    }
}
