package com.gmg.jeukhaeng.area;

import com.gmg.jeukhaeng.area.entity.AreaInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AreaInfoTest {

    @Test
    void getAreaByName1() {
        int area = AreaInfo.getAreaCodeByName("서울특별시");
        assertEquals(1, area);
    }

    @Test
    void getAreaByName2() {
        int area = AreaInfo.getAreaCodeByName("제주특별자치도");
        assertEquals(39, area);
    }

    @Test
    void getSigunguByName() {
        int sigungu = AreaInfo.getSigunguCodeByName("서울특별시", "강남구");
        assertEquals(1, sigungu);
    }
}