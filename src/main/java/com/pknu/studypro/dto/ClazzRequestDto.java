package com.pknu.studypro.dto;

import com.pknu.studypro.util.ClazzDate;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


public class ClazzRequestDto {
    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    public static class Post{
        private String title; //수업 이름
        private String subject; //과목명
        private PostPay postPay; //지급 방식 및 급여
        private Ids ids;

        private List<ClazzTime> clazzTimes;

        public boolean isFixedDatePay() {
            return this.getPostPay().date != null;
        }

        public boolean isRoundPay() {
            return this.getPostPay().round != null;
        }
    }

    @Getter
    public static class PostPay {
        private int amount;
        private Integer date;
        private Integer round;

        // default 값이 있어서 필요 없음
//        private int currentRound;
//        private int totalTime;
    }

    @Getter
    public static class Ids{
        private Long teacherId; //선생님 아이디
        private Long parentId; //부모님 아이디
        private Long studentId; //헉생 아이디
    }

    @Getter
    public static class ClazzTime {
        private Long id; // 식별자
        private ClazzDate clazzDate; //수업하는 날짜
        private LocalTime startTime; //수업 시작 시간
        private LocalTime endTime; // 수업 끝나는 시간
    }
}
