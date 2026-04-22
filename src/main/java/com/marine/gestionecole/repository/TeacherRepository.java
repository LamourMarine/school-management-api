package com.marine.gestionecole.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marine.gestionecole.entity.Teacher;


@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long>{
    Optional<Teacher> findByUserId(Long userId );
}
