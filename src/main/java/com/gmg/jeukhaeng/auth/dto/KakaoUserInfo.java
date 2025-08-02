package com.gmg.jeukhaeng.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KakaoUserInfo {
    private Long id;
    private String email;
    private String nickname;
}
