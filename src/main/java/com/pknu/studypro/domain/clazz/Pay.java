package com.pknu.studypro.domain.clazz;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@DiscriminatorColumn(name = "pay_type")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public abstract class Pay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    protected int amount; // 1회당 금액 or 월급

    @Column(nullable = false)
    protected int totalTime = 0; //총 수업 시간

    @Column(nullable = false)
    protected int currentRound = 0; // 현재 수업 횟수

    protected Pay(final int amount) {
        this.amount = amount;
    }

    public void setPlusCurrentRound() {
        currentRound++;
        // totalTime 추가하기
    }

    public void setMinusCurrentRound() {
        currentRound--;
        // totalTime 추가하기
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
