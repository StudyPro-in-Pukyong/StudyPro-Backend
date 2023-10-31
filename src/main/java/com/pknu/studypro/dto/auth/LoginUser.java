package com.pknu.studypro.dto.auth;

import com.pknu.studypro.domain.member.Role;

public record LoginUser(String username, Role role) {
}
