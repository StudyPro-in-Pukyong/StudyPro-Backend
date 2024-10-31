package com.pknu.studypro.domain.Alert;

import com.pknu.studypro.domain.clazz.Clazz;
import com.pknu.studypro.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Member teacher; // 수신인(선생님)

    @ManyToOne
    private Member parent; // 수신인(학부모)

    @ManyToOne
    private Clazz clazz; // 수업

    @Column(nullable = false)
    private int amount; // 월급

    private LocalDate requestDate; // 요청일

    private LocalDate acceptDate; // 수락일
    private LocalDate settleDate; // 마지막 월급일

    @LastModifiedDate
    @Column(name = "LAST_MODIFIED_AT")
    private LocalDateTime modifiedAt;

    public Alert(Member teacher,
                 Member parent,
                 Clazz clazz,
                 int amount,
                 LocalDate requestDate,
                 LocalDate settleDate) {
        this.teacher = teacher;
        this.parent = parent;
        this.clazz = clazz;
        this.amount = amount;
        this.requestDate = requestDate;
        this.settleDate = settleDate;
    }

    public void accept(LocalDate acceptDay) {
        this.acceptDate = acceptDay;
    }
}
