package com.gmg.jeukhaeng.content.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Content {

    @Id
    private String contentId;

    private String title;

    @Column(length = 1000)
    private String addr;

    @Column(length = 1000)
    private String tel;

    @Column(length = 1000)
    private String zipcode;

    @Column(length = 1000)
    private String firstImage;

    @Column(length = 1000)
    private String firstImage2;

    private String contentTypeId;

    private String areaCode;

    private String sigunguCode;

    private String mapX;

    private String mapY;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
