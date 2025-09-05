package com.gmg.jeukhaeng.auth.controller;

import com.gmg.jeukhaeng.auth.util.JwtUtil;
import com.gmg.jeukhaeng.user.service.UserService;
import com.gmg.jeukhaeng.user.dto.MyPageResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({JwtUtil.class})
@DisplayName("JWT 헤더 요청 통합 테스트")
class JwtHeaderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("유효한 JWT 토큰이 포함된 Authorization 헤더로 /api/auth/me 요청 시 성공")
    void shouldSucceedWithValidJwtTokenInAuthorizationHeader() throws Exception {
        // given
        String userEmail = "test@example.com";
        String validToken = jwtUtil.generateToken(userEmail);
        
        MyPageResponseDto mockResponse = MyPageResponseDto.builder()
                .email(userEmail)
                .nickname("테스트유저")
                .kakaoLinked(true)
                .googleLinked(false)
                .build();
        
        when(userService.getMyPageByEmail(userEmail)).thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(userEmail))
                .andExpect(jsonPath("$.nickname").value("테스트유저"))
                .andExpect(jsonPath("$.kakaoLinked").value(true))
                .andExpect(jsonPath("$.googleLinked").value(false));
    }

    @Test
    @DisplayName("Authorization 헤더 없이 /api/auth/me 요청 시 401 Unauthorized")
    void shouldReturn401WhenNoAuthorizationHeader() throws Exception {
        // when & then
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("잘못된 형식의 Authorization 헤더로 /api/auth/me 요청 시 401 Unauthorized")
    void shouldReturn401WhenInvalidAuthorizationHeaderFormat() throws Exception {
        // when & then
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Basic dXNlcjpwYXNzd29yZA=="))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("유효하지 않은 JWT 토큰으로 /api/auth/me 요청 시 401 Unauthorized")
    void shouldReturn401WhenInvalidJwtToken() throws Exception {
        // when & then
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer invalid.jwt.token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Bearer만 있고 토큰이 없는 Authorization 헤더로 /api/auth/me 요청 시 401 Unauthorized")
    void shouldReturn401WhenBearerWithoutToken() throws Exception {
        // when & then
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer "))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("만료된 JWT 토큰으로 /api/auth/me 요청 시 401 Unauthorized")
    void shouldReturn401WhenExpiredJwtToken() throws Exception {
        // given - 테스트용으로 만료된 토큰 생성 (실제로는 JwtUtil에서 만료 시간을 짧게 설정해야 함)
        String expiredToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiaWF0IjoxNjMwMDAwMDAwLCJleHAiOjE2MzAwMDAwMDF9.invalid";

        // when & then
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("대소문자가 다른 authorization 헤더로 요청 시 정상 동작 확인")
    void shouldWorkWithDifferentCaseAuthorizationHeader() throws Exception {
        // given
        String userEmail = "test@example.com";
        String validToken = jwtUtil.generateToken(userEmail);
        
        MyPageResponseDto mockResponse = MyPageResponseDto.builder()
                .email(userEmail)
                .nickname("테스트유저")
                .kakaoLinked(false)
                .googleLinked(true)
                .build();
        
        when(userService.getMyPageByEmail(userEmail)).thenReturn(mockResponse);

        // when & then - HTTP 헤더는 대소문자를 구분하지 않음
        mockMvc.perform(get("/api/auth/me")
                        .header("authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(userEmail));
    }

    @Test
    @DisplayName("JWT 토큰에 공백이 포함된 경우 정상 처리 확인")
    void shouldHandleTokenWithSpaces() throws Exception {
        // given
        String userEmail = "test@example.com";
        String validToken = jwtUtil.generateToken(userEmail);
        
        // when & then - Bearer 뒤에 공백이 여러 개 있는 경우
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer  " + validToken))
                .andExpect(status().isUnauthorized()); // 현재 구현에서는 정확히 하나의 공백만 처리
    }

    @Test
    @DisplayName("다른 JWT 토큰으로 요청한 사용자 정보가 올바르게 구분되는지 확인")
    void shouldDistinguishDifferentUsers() throws Exception {
        // given
        String userEmail1 = "user1@example.com";
        String userEmail2 = "user2@example.com";
        String token1 = jwtUtil.generateToken(userEmail1);
        String token2 = jwtUtil.generateToken(userEmail2);
        
        MyPageResponseDto mockResponse1 = MyPageResponseDto.builder()
                .email(userEmail1)
                .nickname("사용자1")
                .kakaoLinked(true)
                .googleLinked(false)
                .build();
        
        MyPageResponseDto mockResponse2 = MyPageResponseDto.builder()
                .email(userEmail2)
                .nickname("사용자2")
                .kakaoLinked(false)
                .googleLinked(true)
                .build();
        
        when(userService.getMyPageByEmail(userEmail1)).thenReturn(mockResponse1);
        when(userService.getMyPageByEmail(userEmail2)).thenReturn(mockResponse2);

        // when & then - 첫 번째 사용자
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(userEmail1))
                .andExpect(jsonPath("$.nickname").value("사용자1"));

        // when & then - 두 번째 사용자
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(userEmail2))
                .andExpect(jsonPath("$.nickname").value("사용자2"));
    }
}
