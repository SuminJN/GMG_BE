package com.gmg.jeukhaeng.content.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContentDto {

    @JsonProperty("contentid")
    private String contentId;

    private String title;

    private String addr1;

    private String addr2;

    private String tel;

    private String zipcode;

    @JsonProperty("firstimage")
    private String firstImage;

    @JsonProperty("firstimage2")
    private String firstImage2;

    private String overview;

    private String mapx;

    private String mapy;

    private String contenttypeid;

    private String areacode;

    private String sigungucode;
}
