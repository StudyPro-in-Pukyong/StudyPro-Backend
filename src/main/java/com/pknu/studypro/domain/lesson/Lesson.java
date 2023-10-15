package com.pknu.studypro.domain.lesson;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    private boolean isDone; // 수업 여부

    @Column(nullable = false)
    private String memo; // 메모

    public Lesson(final Long classId, final LocalDateTime startTime, final int minutes, final boolean isDone, final String memo) {
        this.classId = classId;
        this.startTime = startTime;
        this.minutes = minutes;
        this.isDone = isDone;
        this.memo = memo;
    }
}
