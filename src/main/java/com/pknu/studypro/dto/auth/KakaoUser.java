package com.pknu.studypro.dto.auth;

import com.pknu.studypro.domain.member.LoginType;
import com.pknu.studypro.domain.member.Member;
import com.pknu.studypro.domain.member.Role;

public record KakaoUser(String id, Properties properties, kakaoAccount kakao_account) {

    public Member toMember() {
        return new Member(Role.ANONYMOUS, LoginType.KAKAO, id, null, properties().nickname, kakao_account().email, kakao_account().phone_number);
    }

    public static KakaoUser of(final String id, final String nickname, final String email, final String phone_number, final String profile_image) {
        return new KakaoUser(id, new Properties(nickname), new kakaoAccount(email, phone_number, null));
    }

    private record Properties(String nickname) {
    }

    private record kakaoAccount(String email, String phone_number, String profile_image) {
    }
}
