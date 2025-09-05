package com.gmg.jeukhaeng.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * CORS 설정을 담당하는 클래스
 * 
 * 프론트엔드에서 Authorization 헤더를 포함한 요청을 허용하도록 설정
 */
@Configuration
public class CorsConfig {

  @Value("${frontend.redirect-base-url}")
  private String frontendUrl;

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // 허용할 오리진 설정
    configuration.setAllowedOriginPatterns(Arrays.asList(frontendUrl));

    // 허용할 HTTP 메서드
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

    // 허용할 헤더
    configuration.setAllowedHeaders(Arrays.asList(
        "Authorization",
        "Content-Type",
        "X-Requested-With",
        "Accept",
        "Origin",
        "Access-Control-Request-Method",
        "Access-Control-Request-Headers"));

    // 더 이상 쿠키를 사용하지 않으므로 false로 설정
    configuration.setAllowCredentials(false);

    // 노출할 헤더
    configuration.setExposedHeaders(Arrays.asList("Access-Control-Allow-Origin"));

    // 프리플라이트 요청 캐시 시간 (1시간)
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }
}