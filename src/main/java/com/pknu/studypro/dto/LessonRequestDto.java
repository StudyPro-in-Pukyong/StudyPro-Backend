package com.pknu.studypro.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

public class LessonRequestDto {
    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    public static class Post {
        private Long id;
        private Long classId;
        private LocalDateTime startTime;
        private int minutes;
        private boolean isDone;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    public static class Put {
        private Long id;
        private Long classId;
        private LocalDateTime startTime;
        private int minutes;
        private boolean isDone;
    }
}
