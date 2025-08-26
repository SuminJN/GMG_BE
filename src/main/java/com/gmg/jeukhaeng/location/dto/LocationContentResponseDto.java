package com.gmg.jeukhaeng.location.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LocationContentResponseDto {
    private int pageNo;               // 현재 페이지
    private int numOfRows;            // 페이지 크기
    private int totalCount;           // 전체 개수 (TourAPI 응답 기반)
    private int contentId;
    private String title;
    private String addr1;
    private String zipcode;
    private String tel;
    private String contentTypeId;
    private String contentTypeName;   // e.g. "관광지"
    private String areaCode;
    private String sigunguCode;
    private String firstImage;
    private String firstImage2;
    private String mapX;
    private String mapY;}
