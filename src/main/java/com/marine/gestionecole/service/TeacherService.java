package com.marine.gestionecole.service;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.marine.gestionecole.entity.Teacher;
import com.marine.gestionecole.repository.TeacherRepository;

@Service
public class TeacherService {
    final TeacherRepository teacherRepository;

    TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    public Optional<Teacher> findByUserId(Long userId) {
        return teacherRepository.findByUserId(userId);
    }

    public List<Teacher> findAll() {
        return teacherRepository.findAll();
    }

    public Optional<Teacher> findById(Long id) {
        return teacherRepository.findById(id);
    }

    public Teacher save(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    public void deleteById(Long id) {
        teacherRepository.deleteById(id);
    }

    public long count() {
        return teacherRepository.count();
    }

}
