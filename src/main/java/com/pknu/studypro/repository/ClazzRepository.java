package com.pknu.studypro.repository;

import com.pknu.studypro.domain.clazz.Clazz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClazzRepository extends JpaRepository<Clazz, Long> {
    @Query("SELECT c FROM Clazz c WHERE c.teacher.id = :teacherId ORDER BY c.isDone ASC, c.id DESC")
    List<Clazz> findByTeacherId(long teacherId);
    @Query("SELECT c FROM Clazz c WHERE c.student.id = :studentId ORDER BY c.isDone ASC, c.id DESC")
    List<Clazz> findByStudentId(long studentId);
    @Query("SELECT c FROM Clazz c WHERE c.parent.id = :parentId ORDER BY c.isDone ASC, c.id DESC")
    List<Clazz> findByParentId(long parentId);

}
