package com.gmg.jeukhaeng.area.service;

import com.gmg.jeukhaeng.area.dto.AreaContentResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AreaServiceTest {

    @Autowired
    private AreaService areaService;

    @Test
    void getAreaContents() {
        // When: 서울특별시 강남구에 대한 콘텐츠를 요청
        List<AreaContentResponseDto> result = areaService.getAreaContents("서울특별시", "강남구");

        // Then: 결과가 null이 아니고 비어 있지 않음을 확인
        assertNotNull(result, "The result should not be null");

        // Then: 각 콘텐츠의 필드가 null이 아님을 확인
        result.forEach(content -> {
            assertNotNull(content.getContentId(), "Content ID should not be null");
            assertNotNull(content.getTitle(), "Title should not be null");
            assertNotNull(content.getZipcode(), "Zipcode should not be null");
            assertNotNull(content.getTel(), "Tel should not be null");
            assertNotNull(content.getContentTypeId(), "Content Type ID should not be null");
            assertNotNull(content.getMapX(), "Map X should not be null");
            assertNotNull(content.getMapY(), "Map Y should not be null");
            assertNotNull(content.getAddr1(), "Address should not be null");
        });

    }
}