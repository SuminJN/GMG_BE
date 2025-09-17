package com.gmg.jeukhaeng.auth.config;

import com.gmg.jeukhaeng.auth.filter.JwtAuthenticationFilter;
import com.gmg.jeukhaeng.auth.handler.OAuth2LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                
                // CSRF 비활성화 (API 서버일 경우 보통 끔)
                .csrf(csrf -> csrf.disable())

                // 기본 로그인 폼 제거
                .formLogin(form -> form.disable())

                // JWT 기반 인증을 사용하므로, Spring Security가 세션을 생성하거나 저장하지 않도록 설정
                // 세션에 인증 정보를 저장하지 않고, 매 요청마다 토큰으로 인증을 수행
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2.successHandler(oAuth2LoginSuccessHandler))

                // JWT 인증 필터 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // 경로별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/oauth2/authorization/**").permitAll() // 로그인 시작 경로
                        .requestMatchers("/api/auth/test-login").permitAll() // 테스트용 로그인 경로
                        .anyRequest().authenticated());

        return http.build();
    }
}
