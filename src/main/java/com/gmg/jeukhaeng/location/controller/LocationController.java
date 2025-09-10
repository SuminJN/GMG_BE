package com.gmg.jeukhaeng.location.controller;

import com.gmg.jeukhaeng.location.dto.LocationContentResponseDto;
import com.gmg.jeukhaeng.location.model.ContentType;
import com.gmg.jeukhaeng.location.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService locationService;

    /** 위치 기반 검색 */
    @GetMapping
    @Operation(summary = "사용자 위치 기반 콘텐츠 조회", description = "사용자 위치 기반 지역에 해당하는 콘텐츠를 페이징 처리하여 조회합니다.")

    public ResponseEntity<List<LocationContentResponseDto>> getLocationContents(
            @RequestParam String mapX,
            @RequestParam String mapY,
            @RequestParam String radius,
            @RequestParam(required = false, defaultValue = "10") String numOfRows,
            @RequestParam(required = false, defaultValue = "1") String pageNo,
            @RequestParam(required = false, defaultValue = "12") String contentTypeId // 기본: 관광지
    ) {
        List<LocationContentResponseDto> contents = locationService.getLocationContents(
                mapX, mapY, radius, numOfRows, pageNo, contentTypeId
        );
        return ResponseEntity.ok(contents);
    }

    /** 프론트에서 코드/라벨을 알기 쉽게 가져갈 수 있도록 제공 */
    @GetMapping("/content-types")
    @Operation(summary = "contentTypeId 매핑", description = "각 contentTypeId가 무엇인지 조회합니다.(프론트에서 코드/라벨을 알기 쉽게 가져갈 수 있도록 제공)")
    public ResponseEntity<Map<String, String>> getContentTypes() {
        return ResponseEntity.ok(ContentType.asMap());
    }
}
