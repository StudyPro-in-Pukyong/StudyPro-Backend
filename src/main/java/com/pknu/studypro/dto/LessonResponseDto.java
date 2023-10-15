package com.pknu.studypro.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

public class LessonResponseDto {
    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    public static class Response {
        private Long id;
        private Long classId;
        private LocalDateTime startTime;
        private int minutes;
        private boolean isDone;
    }}
