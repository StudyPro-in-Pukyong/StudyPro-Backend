package com.pknu.studypro.domain.clazz;

import lombok.Getter;

@Getter
public enum ClazzDate {
    MON("월"),
    TUE("화"),
    WED("수"),
    THU("목"),
    FRI("금"),
    SAT("토"),
    SUN("일");

    private final String value;

    ClazzDate(String value) {
        this.value = value;
    }
}
