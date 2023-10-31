package com.pknu.studypro.dto.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.pknu.studypro.domain.member.Role;

public record RoleRequest(Role role) {

    @JsonCreator
    public static RoleRequest from(final String role) {
        final Role parsed = Role.valueOf(role.toUpperCase());
        return new RoleRequest(parsed);
    }
}
