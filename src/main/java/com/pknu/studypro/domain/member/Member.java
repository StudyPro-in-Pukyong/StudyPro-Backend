package com.pknu.studypro.domain.member;

import com.pknu.studypro.domain.Alert.Alert;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private LoginType loginType;

    @Column(nullable = false)
    private String username;

    private String password;

    @Column(nullable = false)
    private String nickname;

    private String email;

    private String phoneNumber;

    private String fcmToken;

    public Member(final Role role, final LoginType loginType, final String username,
                  final String password, final String nickname, final String email, final String phoneNumber) {
        this.role = role;
        this.loginType = loginType;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public void changeRole(final Role role) {
        if (this.role != Role.ANONYMOUS) {
            throw new IllegalArgumentException("ANONYMOUS 상태에서만 권한을 수정할 수 있습니다.");
        }

        this.role = role;
    }

    public void setNickname(final String nickname) {
        this.nickname = nickname;
    }

    public void setFcmToken(final String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
