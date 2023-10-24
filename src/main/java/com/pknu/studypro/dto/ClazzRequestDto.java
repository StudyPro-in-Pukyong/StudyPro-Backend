package com.pknu.studypro.dto;

import com.pknu.studypro.domain.clazz.Pay;
import com.pknu.studypro.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


public class ClazzRequestDto {
    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    public static class Post{
        private String title; //수업 이름
        private String subject; //과목명
        private Pay PostPay; //지급 방식 및 급여
        private Ids ids;
    }

    public static class Pay{
        private int amount;
        private int currentRound;
        private int totalTime;
        private int date;
        private int round;
    }

    public static class Ids{
        private Long teacherId; //선생님 아이디
        private Long parentId; //부모님 아이디
        private Long studentId; //헉생 아이디
    }
}
