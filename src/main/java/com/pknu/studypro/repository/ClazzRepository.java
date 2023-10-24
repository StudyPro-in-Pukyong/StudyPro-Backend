package com.pknu.studypro.repository;

import com.pknu.studypro.domain.clazz.Clazz;
import com.pknu.studypro.domain.lesson.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClazzRepository extends JpaRepository<Clazz, Long> {
}
