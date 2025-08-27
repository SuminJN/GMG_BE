package com.gmg.jeukhaeng.area.controller;

import com.gmg.jeukhaeng.area.dto.AreaInfoResponseDto;
import com.gmg.jeukhaeng.area.dto.PagedAreaContentResponseDto;
import com.gmg.jeukhaeng.area.service.AreaService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/areas")
public class AreaController {

    private final AreaService areaService;

    /**
     * 지역별 콘텐츠 조회
     */
    @GetMapping
    @Operation(summary = "지역별 콘텐츠 조회", description = "특정 지역과 시군구에 해당하는 콘텐츠를 페이징 처리하여 조회합니다.")
    public ResponseEntity<PagedAreaContentResponseDto> getAreaContents(
            @RequestParam String area,
            @RequestParam String sigungu,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Received request for area: {}, sigungu: {}, page: {}, size: {}", area, sigungu, page, size);

        Pageable pageable = PageRequest.of(page, size);
        PagedAreaContentResponseDto contents = areaService.getAreaContents(area, sigungu, pageable);
        return ResponseEntity.ok(contents);
    }

    /**
     * 모든 지역 정보 조회
     */
    @GetMapping("/all")
    @Operation(summary = "모든 지역 정보 조회", description = "모든 지역과 해당 시군구 정보를 조회합니다.")
    public ResponseEntity<List<AreaInfoResponseDto>> getAllAreaInfo() {
        log.info("Received request for all area information");

        List<AreaInfoResponseDto> areaInfoList = areaService.getAllAreaInfo();
        return ResponseEntity.ok(areaInfoList);
    }
}
