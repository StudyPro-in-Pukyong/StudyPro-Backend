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
    private Long id;

    @Column(nullable = false)
    private Long classId;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private int minutes;

    @Column(nullable = false)
    private boolean isDone;

    public Lesson(final Long classId, final LocalDateTime startTime, final int minutes, final boolean isDone) {
        this.classId = classId;
        this.startTime = startTime;
        this.minutes = minutes;
        this.isDone = isDone;
    }
}
