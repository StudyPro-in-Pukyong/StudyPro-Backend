package com.pknu.studypro.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


public class ClazzResponseDto {
    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    public static class Response {
        private Long id; // 식별자
        private String title; // 수업 이름
        private String subject; // 과목명
        private ResponsePay responsePay; // 지급 방식 및 급여
//
//        public boolean isFixedDatePay() {
//            return this.getPay().date != null;
//        }
//
//        public boolean isRoundPay() {
//            return this.getPay().round != null;
//        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResponsePay {
        private Long id;
        private int amount;
        private int totalTime;
        private int currentRound;
    }

    @Getter
    @AllArgsConstructor
    public static class ResponseFixedDatePay extends ResponsePay {
        private LocalDate date;

        public ResponseFixedDatePay(Long id, int amount, int totalTime, int currentRound, LocalDate date) {
            super(id, amount, totalTime, currentRound);
            this.date = date;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class ResponseRoundPay extends ResponsePay{
        private int round;

        public ResponseRoundPay(Long id, int amount, int totalTime, int currentRound, int round) {
            super(id, amount, totalTime, currentRound);
            this.round = round;
        }
    }
}
