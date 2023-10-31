package com.pknu.studypro.repository;

import com.pknu.studypro.domain.member.LoginType;
import com.pknu.studypro.domain.member.Member;
import com.pknu.studypro.domain.member.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원의 username으로 조회할 수 있다")
    void findByUsername() {
        //given
        final Member member = saveMember("참치");

        //when
        final Optional<Member> saved = memberRepository.findByUsername("참치");

        //then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(saved).isPresent();
            softAssertions.assertThat(saved.get().getNickname()).isEqualTo(member.getNickname());
        });
    }

    private Member saveMember(final String username) {
        return memberRepository.save(
                new Member(Role.PARENT, LoginType.KAKAO, username, null, "nickname"));
    }

    @Test
    @DisplayName("회원의 username으로 Role을 조회할 수 있다")
    void findRole() {
        //given
        final Member member = saveMember("연어");

        //when
        final Role role = memberRepository.findRoleByUsername("연어");

        //then
        assertThat(role).isEqualTo(member.getRole());
    }

    @Test
    @DisplayName("존재하지 않는 username으로 Role을 조회하면 null을 리턴한다")
    void findRole_fail() {
        //when
        final Role role = memberRepository.findRoleByUsername("연어");

        //then
        assertThat(role).isNull();
    }

}
