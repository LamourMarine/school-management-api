package com.marine.gestionecole.service;
import com.marine.gestionecole.entity.Grade;
import com.marine.gestionecole.repository.GradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GradeService {
    
    @Autowired
    private GradeRepository repository;

    //Recuperer toutes les notes
    public List<Grade> findAll() {
        return repository.findAll();
    }

    // Recuperer une note par ID
    public Optional<Grade> findById(Long id) {
        return repository.findById(id);
    }

    //Récuperer les notes d'un étudiant
    public List<Grade>findByStudentId(Long studentId) {
        return repository.findByStudentId(studentId);
    }

    //Récuperer les notes d'un cours
    public List<Grade>findByCourseId(Long courseId) {
        return repository.findByCourseId(courseId);
    }

    //Créer ou modifier une note
    public Grade save(Grade grade) {
        return repository.save(grade);
    }

    //Supprimer une note
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    //Compter le nombre de notes
    public long count() {
        return repository.count();
    }
}
