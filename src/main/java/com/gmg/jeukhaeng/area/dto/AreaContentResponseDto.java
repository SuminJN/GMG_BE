package com.gmg.jeukhaeng.area.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AreaContentResponseDto {

    private int contentId;
    private String title;
    private String addr1;
    private String zipcode;
    private String tel;
    private String contentTypeId;
    private String areaCode;
    private String sigunguCode;
    private String firstImage;
    private String firstImage2;
    private String mapX;
    private String mapY;
}
