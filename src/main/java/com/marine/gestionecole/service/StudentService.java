package com.marine.gestionecole.service;

import com.marine.gestionecole.dto.StudentResponse;
import com.marine.gestionecole.entity.Grade;
import com.marine.gestionecole.entity.Student;
import com.marine.gestionecole.repository.GradeRepository;
import com.marine.gestionecole.repository.StudentRepository;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

  final StudentRepository studentRepository;
  final GradeRepository gradeRepository;

  StudentService(
    StudentRepository studentRepository,
    GradeRepository gradeRepository
  ) {
    this.studentRepository = studentRepository;
    this.gradeRepository = gradeRepository;
  }

  // Récupérer tous les étudiants
  public List<StudentResponse> findAll() {
    return studentRepository
      .findAll()
      .stream()
      .map(student -> {
        StudentResponse response = new StudentResponse();
        response.setId(student.getId());
        response.setFirstName(student.getFirstName());
        response.setLastName(student.getLastName());

        OptionalDouble avg = gradeRepository
          .findByStudentId(student.getId())
          .stream()
          .mapToDouble(Grade::getScore)
          .average();

        response.setAverage(avg.isPresent() ? avg.getAsDouble() : null);
        return response;
      })
      .toList();
  }

  // Récupérer un étudiant par ID
  public Optional<Student> findById(Long id) {
    return studentRepository.findById(id);
  }

  // Créer ou modifier un étudiant
  public Student save(Student student) {
    return studentRepository.save(student);
  }

  // Supprimer un étudiant
  public void deleteById(Long id) {
    studentRepository.deleteById(id);
  }

  // Compter le nombre d'étudiants
  public long count() {
    return studentRepository.count();
  }
}
