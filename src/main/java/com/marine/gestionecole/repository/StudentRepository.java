package com.marine.gestionecole.repository;

import com.marine.gestionecole.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    // Pas besoin de code , JpaRepository fournit :
    // - findAll()
    // - findById(Long id)
    // - save(Etudiant etudiant)
    // - deleteById(Long id)
    // - count()
    // etc.
}