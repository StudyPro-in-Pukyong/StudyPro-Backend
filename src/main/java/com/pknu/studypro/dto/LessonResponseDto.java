package com.pknu.studypro.dto;

import com.pknu.studypro.domain.lesson.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class LessonResponseDto {
    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    public static class Response {
        private Long id;
        private Long classId; // 연관관계에 있는 class
        private LocalDateTime startTime; // 수업 시작 시간
        private int minutes; // 수업시간(분 단위)
        private Boolean isDone; // 수업 여부
        private Type type; // 수업 유형
        private String progress; // 진도
        private final List<String> homeworks = new ArrayList<>(); // 숙제
    }

    // id=16, type=MAKEUP, progress=수학(상) 경우의 수, date=2024-11-24, startTime=오후 2:00, endTime=오후 3:00, homeworks
    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    public static class ModelResponse {
        private Long id;
        private LocalDate date; // 수업 날짜
        private LocalTime startTime; // 수업 시작 시간
        private LocalTime endTime; // 수업 종료 시간
        private Boolean isDone; // 수업 여부
        private Type type; // 수업 유형
        private String progress; // 진도
        private final List<String> homeworks = new ArrayList<>(); // 숙제
    }
}
