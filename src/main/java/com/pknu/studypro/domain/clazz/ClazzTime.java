package com.pknu.studypro.domain.clazz;

import com.pknu.studypro.domain.clazz.Clazz;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class ClazzTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Clazz clazz;

    @Column(nullable = false)
    private String clazzDate; // 수업날짜

    @Column(nullable = false)
    private int startTime;

    @Column(nullable = false)
    private int endTime;

    public ClazzTime(final Clazz clazz, final String date, final int startTime, final int endTime) {
        this.clazz = clazz;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
