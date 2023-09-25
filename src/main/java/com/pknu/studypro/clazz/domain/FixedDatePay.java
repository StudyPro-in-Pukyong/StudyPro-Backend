package com.pknu.studypro.clazz.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class FixedDatePay extends Pay {

    @Column(nullable = false)
    private LocalDate date;

    public FixedDatePay(final int amount, final LocalDate date) {
        super(amount);
        this.date = date;
    }
}

