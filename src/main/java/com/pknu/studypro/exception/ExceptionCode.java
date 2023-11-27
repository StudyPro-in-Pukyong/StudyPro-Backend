
package com.pknu.studypro.exception;
import lombok.Getter;

import java.util.Arrays;

public enum ExceptionCode {
    NOT_ALLOW_SETTLE(400, "Not Allow Settle"),
    NOT_TEACHER(403, "Forbidden Because User not Teacher"),
    MEMBER_NOT_FOUND(404, "Member not found"),
    CLASS_NOT_FOUND(404, "Class not found"),
    LESSON_NOT_FOUND(404, "Lesson not found"),
    CLASS_POST_FAIL(500, "PostDto convert fail"),
    NOT_CHANGE_PAY_TYPE(500, "Can't change pay_type");

    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int code, String message) {
        this.status = code;
        this.message = message;
    }

    public static ExceptionCode findByMessage(String message) {
        return Arrays.stream(ExceptionCode.values())
                .filter(exceptionCode -> exceptionCode.getMessage().equals(message))
                .findFirst()
                .orElse(null);
    }
}
