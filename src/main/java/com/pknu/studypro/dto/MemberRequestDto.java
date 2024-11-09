package com.pknu.studypro.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class MemberRequestDto {
    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    public static class Put {
        private Long id;
        private String nickname;
    }

    public static record Post(String fcmToken) {}
}
