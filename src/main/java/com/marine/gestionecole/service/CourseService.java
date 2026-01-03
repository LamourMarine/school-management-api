package com.marine.gestionecole.service;
import com.marine.gestionecole.entity.Course;
import com.marine.gestionecole.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    
    @Autowired
    private CourseRepository repository;

    // Recuperer tous les cours
    public List<Course> findAll() {
        return repository.findAll();
    }

    // Recuperer un cours par ID
    public Optional<Course> findById(Long id) {
        return repository.findById(id);
    }

    // Creer ou modifier un cours
    public Course save(Course course) {
        return repository.save(course);
    }

    // Supprimer un cours
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    // Compter le nombre de cours
    public long count() {
        return repository.count();
    }
}
