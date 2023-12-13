package com.pknu.studypro.domain.clazz;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
public class FixedDatePay extends Pay { // 지정일

//    @Column(nullable = false)
    @Column
    @Max(31)
    @Min(1)
    private int date; // 월급 지정일

    public FixedDatePay(final int amount, final int date) {
        super(amount);
        this.date = date;
    }

    public void setDate(int date) {
        this.date = date;
    }
}

