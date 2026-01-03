package com.marine.gestionecole.service;

import com.marine.gestionecole.entity.Student;
import com.marine.gestionecole.repository.StudentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    
    @Autowired
    private StudentRepository repository;
    
    // Récupérer tous les étudiants
    public List<Student> findAll() {
        return repository.findAll();
    }
    
    // Récupérer un étudiant par ID
    public Optional<Student> findById(Long id) {
        return repository.findById(id);
    }
    
    // Créer ou modifier un étudiant
    public Student save(Student student) {
        return repository.save(student);
    }
    
    // Supprimer un étudiant
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
    
    // Compter le nombre d'étudiants
    public long count() {
        return repository.count();
    }
}