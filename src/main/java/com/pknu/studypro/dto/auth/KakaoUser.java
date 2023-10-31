package com.pknu.studypro.dto.auth;

import com.pknu.studypro.domain.member.LoginType;
import com.pknu.studypro.domain.member.Member;
import com.pknu.studypro.domain.member.Role;

public record KakaoUser(String id, Properties properties) {

    public Member toMember() {
        return new Member(Role.ANONYMOUS, LoginType.KAKAO, id, null, properties().nickname);
    }

    public static KakaoUser of(final String id, final String nickname) {
        return new KakaoUser(id, new Properties(nickname));
    }

    private record Properties(String nickname) {
    }
}
