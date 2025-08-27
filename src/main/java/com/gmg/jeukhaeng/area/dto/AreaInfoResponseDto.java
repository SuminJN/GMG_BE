package com.gmg.jeukhaeng.area.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AreaInfoResponseDto {
    private String areaName;
    private Map<String, String> sigunguMap;
}
