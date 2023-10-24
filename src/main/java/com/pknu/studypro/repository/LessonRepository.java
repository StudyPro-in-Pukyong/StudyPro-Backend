package com.pknu.studypro.repository;

import com.pknu.studypro.domain.lesson.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findByStartTimeBetween(LocalDateTime start, LocalDateTime finish);
}
