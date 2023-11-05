package com.pknu.studypro.repository;

import com.pknu.studypro.domain.clazz.Clazz;
import com.pknu.studypro.domain.lesson.Lesson;
import com.pknu.studypro.domain.member.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClazzRepository extends JpaRepository<Clazz, Long> {
    List<Clazz> findByTeacherId(long teacher);
}
