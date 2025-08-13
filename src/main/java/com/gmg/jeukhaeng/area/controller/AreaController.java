package com.gmg.jeukhaeng.area.controller;

import com.gmg.jeukhaeng.area.dto.AreaContentResponseDto;
import com.gmg.jeukhaeng.area.service.AreaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/area")
public class AreaController {

    private final AreaService areaService;

    @GetMapping
    public ResponseEntity<List<AreaContentResponseDto>> getAreaContents(@RequestParam String area,@RequestParam String sigungu) {

        log.info("Received request for area: {}, sigungu: {}", area, sigungu);

        List<AreaContentResponseDto> contents = areaService.getAreaContents(area, sigungu);
        return ResponseEntity.ok(contents);
    }
}
