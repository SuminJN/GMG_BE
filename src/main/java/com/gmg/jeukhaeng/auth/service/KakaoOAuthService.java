package com.gmg.jeukhaeng.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmg.jeukhaeng.auth.config.OAuthProperties;
import com.gmg.jeukhaeng.auth.dto.KakaoUserInfo;
import com.gmg.jeukhaeng.auth.util.JwtUtil;
import com.gmg.jeukhaeng.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoOAuthService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JwtUtil jwtUtil;
    private final OAuthProperties properties;
    private final UserService userService;

    public String processKakaoLogin(String code) {
        // 1. 토큰 요청
        String accessToken = getAccessToken(code);

        // 2. 사용자 정보 요청
        KakaoUserInfo userInfo = getUserInfo(accessToken);

        // 3. 사용자 정보로 회원가입 or 로그인
        userService.findOrCreate(
                "kakao",
                String.valueOf(userInfo.getId()),
                userInfo.getEmail(),
                userInfo.getNickname()
        );

        // 4. JWT 발급
        return jwtUtil.generateToken(userInfo.getEmail());
    }

    private String getAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", properties.getClientId());
        params.add("redirect_uri", properties.getRedirectUri());
        params.add("code", code);

        HttpEntity<?> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(properties.getTokenUri(), request, String.class);

        try {
            JsonNode json = objectMapper.readTree(response.getBody());
            return json.get("access_token").asText();
        } catch (Exception e) {
            throw new RuntimeException("토큰 파싱 실패", e);
        }
    }

    private KakaoUserInfo getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                properties.getUserInfoUri(),
                HttpMethod.GET,
                request,
                String.class
        );

        try {
            JsonNode json = objectMapper.readTree(response.getBody());
            Long id = json.get("id").asLong();
            String email = json.get("kakao_account").get("email").asText();
            String nickname = json.get("properties").get("nickname").asText();
            log.info("Kakao User Info - ID: {}, Email: {}, Nickname: {}", id, email, nickname);
            return new KakaoUserInfo(id, email, nickname);
        } catch (Exception e) {
            throw new RuntimeException("유저 정보 파싱 실패", e);
        }
    }
}
