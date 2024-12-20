package com.pknu.studypro.dto;

import com.pknu.studypro.domain.clazz.ClazzDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;


public class ClazzResponseDto {
    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    public static class Response {
        private Long id; // 식별자
        private String title; // 수업 이름
        private String subject; // 과목명
        private Boolean isDone; // 클래스 종료 여부
        private ResponsePay responsePay; // 지급 방식 및 급여
        private Ids ids; // 관련 사람들 id
        private List<ClazzTime> clazzTimes; // 수업 일정
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
        private int date;

        public ResponseFixedDatePay(Long id, int amount, int totalTime, int currentRound, int date) {
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

    @Getter
    @AllArgsConstructor
    public static class Ids{
        private Long teacherId; //선생님 아이디
        private Long parentId; //부모님 아이디
        private Long studentId; //헉생 아이디
    }

    @Getter
    @AllArgsConstructor
    public static class ClazzTime{
        private Long id; // 식별자
        private ClazzDate clazzDate; //수업하는 날짜
        private LocalTime startTime; //수업 시작 시간
        private LocalTime endTime; // 수업 끝나는 시간
    }
}
