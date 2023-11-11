package com.pknu.studypro.domain.lesson;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 식별자

    @Column(nullable = false)
    private Long classId; // 연관관계에 있는 class

    @Column(nullable = false)
    private LocalDateTime startTime; // 수업 시작 시간

    @Column(nullable = false)
    private int minutes; // 수업시간(분 단위)

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Type type; // 수업 유형

    @Column(nullable = false)
    private Boolean isDone; // 수업 여부

    @Column
    private String progress; // 진도

    @Column
    @ElementCollection(fetch = FetchType.EAGER)
    private final List<String> homeworks = new ArrayList<>(); // 숙제

    public Lesson(final Long id, final Long classId, final LocalDateTime startTime, final int minutes, final Type type, final boolean isDone, final String progress) {
        this.id = id;
        this.classId = classId;
        this.startTime = startTime;
        this.minutes = minutes;
        this.type = type;
        this.isDone = isDone;
        this.progress = progress;
    }

    public void setIsDone(boolean isDone) {
        this.isDone = isDone;
    }
}
