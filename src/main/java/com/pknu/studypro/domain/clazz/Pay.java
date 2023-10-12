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
    protected int amount;
    protected int currentRound;

    protected Pay(final int amount) {
        this.amount = amount;
    }
}
