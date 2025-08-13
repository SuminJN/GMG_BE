package com.gmg.jeukhaeng.area.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.gmg.jeukhaeng.area.dto.AreaContentResponseDto;
import com.gmg.jeukhaeng.area.entity.AreaInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AreaService {

    @Value("${tour-api-service-key}")
    private String SERVICE_KEY;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<AreaContentResponseDto> getAreaContents(String area, String sigungu) {

        int areaCodeByName = AreaInfo.getAreaCodeByName(area);
        int sigunguCodeByName = AreaInfo.getSigunguCodeByName(area, sigungu);

        URI uri = UriComponentsBuilder
                .fromHttpUrl("https://apis.data.go.kr/B551011/KorService2/areaBasedList2")
                .queryParam("serviceKey", SERVICE_KEY)
                .queryParam("MobileApp", "test")
                .queryParam("MobileOS", "ETC")
                .queryParam("_type", "json")
                .queryParam("areaCode", areaCodeByName)
                .queryParam("sigunguCode", sigunguCodeByName)
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

        if (response.getStatusCode() == HttpStatus.OK) {

            JsonNode items = response.getBody().path("response").path("body").path("items");

            if (items.isMissingNode() || items.isEmpty()) {
                return List.of();  // items가 없거나 비어있으면 빈 리스트 반환
            }

            List<AreaContentResponseDto> result = new ArrayList<>();

            for(JsonNode item : items.path("item")) {
                AreaContentResponseDto content = new AreaContentResponseDto();
                content.setContentId(item.path("contentid").asInt());
                content.setTitle(item.path("title").asText());
                content.setAddr1(item.path("addr1").asText());
                content.setZipcode(item.path("zipcode").asText());
                content.setTel(item.path("tel").asText());
                content.setContentTypeId(item.path("contenttypeid").asText());
                content.setAreaCode(item.path("areacode").asText());
                content.setSigunguCode(item.path("sigungucode").asText());
                content.setFirstImage(item.path("firstimage").asText());
                content.setFirstImage2(item.path("firstimage2").asText());
                content.setMapX(item.path("mapx").asText());
                content.setMapY(item.path("mapy").asText());

                result.add(content);
            }

            return result;

        } else {
            throw new RuntimeException("Failed to fetch area contents: " + response.getStatusCode());
        }
    }
}
