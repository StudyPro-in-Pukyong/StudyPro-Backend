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
        private Long id;
        private String title; //수업 이름
        private String subject; //과목명
        private Boolean isDone; // 클래스 종료 여부
        private PostPay postPay; //지급 방식 및 급여
        private Ids ids; // 관련 사람들 id

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
