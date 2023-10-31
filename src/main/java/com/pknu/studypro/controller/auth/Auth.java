package com.pknu.studypro.controller.auth;

import com.pknu.studypro.domain.member.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auth {
    Role role() default Role.ANONYMOUS;
}
