package com.gmg.jeukhaeng.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (API 서버일 경우 보통 끔)
                .csrf(csrf -> csrf.disable())
                // 기본 로그인 폼 제거
                .formLogin(form -> form.disable())
                // 세션 비활성화 (JWT 방식으로 바꾸는 경우)
                .sessionManagement(session -> session.disable())
                // 경로별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/oauth/**").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}
