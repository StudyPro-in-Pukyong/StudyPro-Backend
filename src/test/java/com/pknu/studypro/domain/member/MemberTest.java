package com.pknu.studypro.domain.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberTest {

    @Test
    @DisplayName("권한을 변경할 수 있다")
    void changeRole() {
        //given
        final Member member = new Member(Role.ANONYMOUS, LoginType.KAKAO,
                "username", null, "nickname", "email", "phoneNumber");

        //when
        member.changeRole(Role.STUDENT);

        //then
        assertThat(member.getRole()).isEqualTo(Role.STUDENT);
    }

    @Test
    @DisplayName("ANONYMOUS가 아니면 권한을 변경할 때 예외가 발생한다")
    void changeRole_fail() {
        //given
        final Member member = new Member(Role.STUDENT, LoginType.KAKAO,
                "username", null, "nickname", "email", "phoneNumber");

        //when, then
        assertThatThrownBy(() -> member.changeRole(Role.TEACHER))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
