package com.pknu.studypro.domain.clazz;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class FixedDatePay extends Pay { // 지정일

//    @Column(nullable = false)
    @Column
    private int date; // 월급 지정일

    public FixedDatePay(final int amount, final int date) {
        super(amount);
        this.date = date;
    }
}

