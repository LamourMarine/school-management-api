package com.marine.gestionecole.repository;
import com.marine.gestionecole.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByStudentId(Long etudiantId);
    List<Grade> findByCourseId(Long coursId);
}
    