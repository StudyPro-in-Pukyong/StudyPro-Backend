package com.pknu.studypro.dto;

import com.pknu.studypro.domain.lesson.Type;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LessonRequestDto {
    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    public static class Post {
        private Long classId; // 연관관계에 있는 class
        private LocalDateTime startTime; // 수업 시작 시간
        private int minutes; // 수업시간(분 단위)
        private boolean isDone; // 수업 여부
        private Type type; // 수업 유형
        private String progress; // 진도
        private final List<String> homeworks = new ArrayList<>(); // 숙제
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    public static class Put {
        private Long id;
        private Long classId; // 연관관계에 있는 class
        private LocalDateTime startTime; // 수업 시작 시간
        private int minutes; // 수업시간(분 단위)
        private boolean isDone; // 수업 여부
        private Type type; // 수업 유형
        private String progress; // 진도
        private final List<String> homeworks = new ArrayList<>(); // 숙제
    }
}
