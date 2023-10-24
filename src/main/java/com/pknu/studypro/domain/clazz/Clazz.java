package com.pknu.studypro.domain.clazz;

import com.pknu.studypro.domain.member.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @OneToOne(optional = false)
    private Member teacher;
    @OneToOne
    private Member parent;
    @OneToOne
    private Member student;

    public Clazz(final Pay pay, final String title, final String subject, final Member teacher, final Member parent, final Member student) {
        this.pay = pay;
        this.title = title;
        this.subject = subject;
        this.teacher = teacher;
        this.parent = parent;
        this.student = student;
    }

    public void setParent(final Member parent) {
        this.parent = parent;
    }

    public void setStudent(final Member student) {
        this.student = student;
    }
}
