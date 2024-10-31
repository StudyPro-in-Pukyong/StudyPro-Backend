package com.pknu.studypro.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

public class AlertResponseDto {
    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    public static class Response {
        private Long id;
        private String classTitle; // 클래스 이름
        private int amount; // 월급
        private LocalDate requestDate; // 요청일
        private LocalDate acceptDate; // 수락일
        private LocalDate settleDate; // 마지막 월급일
    }
}
