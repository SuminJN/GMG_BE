package com.gmg.jeukhaeng.location.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.gmg.jeukhaeng.area.dto.AreaContentResponseDto;
import com.gmg.jeukhaeng.location.dto.LocationContentResponseDto;
import com.gmg.jeukhaeng.location.model.ContentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
@Slf4j

@Service
@RequiredArgsConstructor
public class LocationService {

    @Value("${tour-api-service-key}")
    private String SERVICE_KEY;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<LocationContentResponseDto> getLocationContents(
            String mapX,
            String mapY,
            String radius,
            String numOfRows,
            String pageNo,
            String contentTypeId
    ) {
        // 기본값 처리
        if (radius == null || radius.isBlank()) radius = "1000";
        if (numOfRows == null || numOfRows.isBlank()) numOfRows = "10";
        if (pageNo == null || pageNo.isBlank()) pageNo = "1";
        if (contentTypeId == null || contentTypeId.isBlank()) contentTypeId = "12"; // 관광지

        URI uri = UriComponentsBuilder
                .fromHttpUrl("https://apis.data.go.kr/B551011/KorService2/locationBasedList2")
                .queryParam("serviceKey", SERVICE_KEY)
                .queryParam("MobileApp", "test")
                .queryParam("MobileOS", "ETC")
                .queryParam("_type", "json")
                .queryParam("mapX", mapX)
                .queryParam("mapY", mapY)
                .queryParam("radius", radius)
                .queryParam("numOfRows", numOfRows)
                .queryParam("pageNo", pageNo)
                .queryParam("contentTypeId", contentTypeId)
                .build(true)
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                entity,
                JsonNode.class
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to fetch location contents: " + response.getStatusCode());
        }

        JsonNode body = response.getBody()
                .path("response")
                .path("body");

        int totalCount = body.path("totalCount").asInt(0);

        List<LocationContentResponseDto> result = new ArrayList<>();
        JsonNode items = body.path("items");

        if (!items.isMissingNode() && !items.isEmpty()) {
            for (JsonNode item : items.path("item")) {
                LocationContentResponseDto dto = LocationContentResponseDto.builder()
                        .pageNo(Integer.parseInt(pageNo))
                        .numOfRows(Integer.parseInt(numOfRows))
                        .totalCount(totalCount)
                        .contentId(item.path("contentid").asInt())
                        .title(item.path("title").asText())
                        .addr1(item.path("addr1").asText())
                        .zipcode(item.path("zipcode").asText())
                        .tel(item.path("tel").asText())
                        .contentTypeId(item.path("contenttypeid").asText())
                        .contentTypeName(ContentType.nameByCode(item.path("contenttypeid").asText())) // enum 사용
                        .areaCode(item.path("areacode").asText())
                        .sigunguCode(item.path("sigungucode").asText())
                        .firstImage(item.path("firstimage").asText())
                        .firstImage2(item.path("firstimage2").asText())
                        .mapX(item.path("mapx").asText())
                        .mapY(item.path("mapy").asText())
                        .build();

                result.add(dto);
            }
        }

        return result;
    }
}
