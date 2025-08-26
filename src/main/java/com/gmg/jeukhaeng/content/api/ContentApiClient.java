package com.gmg.jeukhaeng.content.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmg.jeukhaeng.content.dto.ContentDto;
import com.gmg.jeukhaeng.content.entity.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.List;

@Slf4j
@Component
public class ContentApiClient {

    private final RestClient restClient = RestClient.create();


    @Value("${api.api-base-url}")
    private String baseUrl;
    @Value("${api.encoded-service-key}")
    private String encodedServiceKey;

    public List<ContentDto> fetchContents(int page, int areaCode) {
        String uri = baseUrl
                + "?serviceKey=" + encodedServiceKey
                + "&MobileOS=ETC"
                + "&MobileApp=AppTest"
                + "&_type=json"
                + "&numOfRows=1000"
                + "&pageNo=" + page
                + "&arrange=C"
                + "&areaCode=" + areaCode;

        log.info("요청 URI: {}", uri);

        try {
            ApiResponse response = restClient.get()
                    .uri(URI.create(uri))  // ← 여기서 더 이상 인코딩 안 함
                    .header("Accept", "application/json")
                    .retrieve()
                    .body(ApiResponse.class);

            if (response == null || response.getResponse() == null) {
                log.warn("API 응답이 null입니다.");
                return List.of();
            }

            return response.getResponse().getBody().getItems().getItem();
        } catch (Exception e) {
            log.error("API 호출 실패 (page {}, areaCode {}): {}", page, areaCode, e.getMessage(), e);
            return List.of();
        }
    }
    public int fetchTotalCount(int areaCode) {
        String uri = baseUrl
                + "?serviceKey=" + encodedServiceKey
                + "&MobileOS=ETC"
                + "&MobileApp=AppTest"
                + "&_type=json"
                + "&numOfRows=1000"
                + "&pageNo=1"
                + "&arrange=C"
                + "&areaCode=" + areaCode;
        log.info("요청 URI: {}", uri);

        try {
            var responseEntity = restClient.get()
                    .uri(URI.create(uri))  // ← 여기서 더 이상 인코딩 안 함
                    .header("Accept", "application/json")
                    .retrieve()
                    .toEntity(String.class); // 먼저 raw 문자열로 받는다

            String contentType = responseEntity.getHeaders().getContentType().toString();
            log.info("응답 Content-Type = {}", contentType);
            log.info("응답 body = {}", responseEntity.getBody());

            // JSON일 때만 수동 파싱
            if (contentType.contains("application/json")) {
                // Jackson 직접 수동 파싱
                ObjectMapper mapper = new ObjectMapper();
                ApiResponse parsed = mapper.readValue(responseEntity.getBody(), ApiResponse.class);
                return parsed.getResponse().getBody().getTotalCount();
            }

            log.warn("JSON이 아닌 응답이 왔습니다. areaCode={}", areaCode);
        } catch (Exception e) {
            log.error("총 개수 조회 실패 (areaCode {}): {}", areaCode, e.getMessage(), e);
        }

        return 0;
    }


}
