package com.pknu.studypro.domain.clazz;

import com.pknu.studypro.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Clazz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true, optional = false)
    private Pay pay;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String subject;

    @ManyToOne
    private Member teacher;
    @ManyToOne
    private Member parent;
    @ManyToOne
    private Member student;

    @OneToMany(mappedBy = "clazz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClazzTime> clazzTimes; // 수업일정

    private boolean isDone = false; // 클래스 종료 여부
    private LocalDateTime settleRequestDate; // 월급 요청일
    private LocalDateTime settleResponseDate; // 월급일

    public Clazz(final Long id, final Pay pay, final String title, final String subject, final Member teacher, final Member parent, final Member student,  final List<ClazzTime> clazzTimes) {
        this.id = id;
        this.pay = pay;
        this.title = title;
        this.subject = subject;
        this.teacher = teacher;
        this.parent = parent;
        this.student = student;
        this.clazzTimes = clazzTimes;
    }

    public void setTeacher(final Member teacher) {
        this.teacher = teacher;
    }

    public void setParent(final Member parent) {
        this.parent = parent;
    }

    public void setIsDone(final boolean isDone) {
        this.isDone = isDone;
    }

    public void setStudent(final Member student) {
        this.student = student;
    }

    public void setClassId(final long id) {this.id = id;}
}
