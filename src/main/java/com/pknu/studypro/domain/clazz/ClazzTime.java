package com.pknu.studypro.domain.clazz;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class ClazzTime { // 수업일정

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Clazz clazz;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ClazzDate clazzDate; //수업하는 요일

    @Column(nullable = false)
    private LocalTime startTime; //수업 시작 시간

    @Column(nullable = false)
    private LocalTime endTime; // 수업 끝나는 시간

    public ClazzTime(Long id, Clazz clazz, ClazzDate clazzDate, LocalTime startTime, LocalTime endTime) {
        this.id = id;
        this.clazz = clazz;
        this.clazzDate = clazzDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void setClazz(Clazz clazz) {
        this.clazz = clazz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // 자기 자신과 비교하면 true
        if (o == null || getClass() != o.getClass()) return false; // null값이 들어오거나 다른 클래스가 들어오면 false

        ClazzTime clazzTime = (ClazzTime) o;
        return clazzDate == clazzTime.getClazzDate() &&
                startTime == clazzTime.getStartTime() &&
                endTime == clazzTime.getEndTime();
    }
}
