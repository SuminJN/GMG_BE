package com.gmg.jeukhaeng.location.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum ContentType {
    TOURIST_SPOT("12", "관광지"),
    CULTURAL_FACILITY("14", "문화시설"),
    FESTIVAL("15", "축제공연행사"),
    TRAVEL_COURSE("25", "여행코스"),
    LEISURE("28", "레포츠"),
    ACCOMMODATION("32", "숙박"),
    SHOPPING("38", "쇼핑"),
    RESTAURANT("39", "음식점");

    private final String code;
    private final String name;

    public static String nameByCode(String code) {
        return Arrays.stream(values())
                .filter(v -> v.code.equals(code))
                .map(ContentType::getName)
                .findFirst()
                .orElse("알 수 없음");
    }

    /** 프론트에서 셀렉트 박스 만들 때 쓰기 좋게 전체 맵 제공 */
    public static Map<String, String> asMap() {
        Map<String, String> m = new LinkedHashMap<>();
        for (ContentType ct : values()) {
            m.put(ct.code, ct.name);
        }
        return m;
    }
}
