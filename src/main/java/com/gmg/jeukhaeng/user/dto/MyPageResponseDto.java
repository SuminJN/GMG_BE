package com.gmg.jeukhaeng.user.dto;

import com.gmg.jeukhaeng.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyPageResponseDto {
    private String email;
    private String nickname;
    private boolean kakaoLinked;
    private boolean googleLinked;

    public static MyPageResponseDto from(User u) {
        return MyPageResponseDto.builder()
                .email(u.getEmail())
                .nickname(u.getNickname())
                .kakaoLinked(isLinked(u, "KAKAO"))
                .googleLinked(isLinked(u, "GOOGLE"))
                .build();
    }

    private static boolean isLinked(User u, String provider) {
        if (u.getProvider() != null && u.getProvider().equalsIgnoreCase(provider)) return true;
        return u.getLinkedProviders() != null
                && u.getLinkedProviders().stream()
                .anyMatch(p -> p != null && p.equalsIgnoreCase(provider));
    }
}
