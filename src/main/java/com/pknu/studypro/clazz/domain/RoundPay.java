package com.pknu.studypro.clazz.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RoundPay extends Pay {

    @Column(nullable = false)
    private int round;

    public RoundPay(final int amount, final int round) {
        super(amount);
        this.round = round;
    }
}
