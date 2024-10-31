package com.pknu.studypro.repository;

import com.pknu.studypro.domain.Alert.Alert;
import com.pknu.studypro.domain.member.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findAllByTeacherOrderByModifiedAtDesc(Member teacher, Pageable pageable); // 선생님을 바탕으로 알람 조회
    List<Alert> findAllByParentOrderByModifiedAtDesc(Member parent, Pageable pageable); // 학부모를 바탕으로 알람 조회
}
