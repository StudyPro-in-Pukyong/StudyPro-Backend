package com.pknu.studypro.repository;

import com.pknu.studypro.domain.member.Member;
import com.pknu.studypro.domain.member.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUsername(final String username);
    Optional<Member> findByIdAndRole(Long memberId, Role role);
    List<Member> findByNicknameAndRole(String nickname, Role role);

    @Query("""
            SELECT m.role
            FROM Member m
            WHERE m.username = :username
            """)
    Role findRoleByUsername(@Param("username") final String username);
}
