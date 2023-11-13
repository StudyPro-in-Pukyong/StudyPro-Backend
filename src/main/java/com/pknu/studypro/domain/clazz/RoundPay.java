package com.pknu.studypro.domain.clazz;

import com.pknu.studypro.domain.clazz.Pay;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
public class RoundPay extends Pay { // 회차

//    @Column(nullable = false)
    @Column
    private int round; // 회차

    public RoundPay(final int amount, final int round) {
        super(amount);
        this.round = round;
    }
}
