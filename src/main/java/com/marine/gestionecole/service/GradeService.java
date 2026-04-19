package com.marine.gestionecole.service;

import com.marine.gestionecole.entity.Grade;
import com.marine.gestionecole.repository.GradeRepository;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import org.springframework.stereotype.Service;

@Service
public class GradeService {

  final GradeRepository gradeRepository;

  GradeService(GradeRepository gradeRepository) {
    this.gradeRepository = gradeRepository;
  }

  //Recuperer toutes les notes
  public List<Grade> findAll() {
    return gradeRepository.findAll();
  }

  // Recuperer une note par ID
  public Optional<Grade> findById(Long id) {
    return gradeRepository.findById(id);
  }

  //Récuperer les notes d'un étudiant
  public List<Grade> findByStudentId(Long studentId) {
    return gradeRepository.findByStudentId(studentId);
  }

  //Récuperer les notes d'un cours
  public List<Grade> findByCourseId(Long courseId) {
    return gradeRepository.findByCourseId(courseId);
  }

  //Créer ou modifier une note
  public Grade save(Grade grade) {
    return gradeRepository.save(grade);
  }

  //Supprimer une note
  public void deleteById(Long id) {
    gradeRepository.deleteById(id);
  }

  //Compter le nombre de notes
  public long count() {
    return gradeRepository.count();
  }

  // Recuperer la moyenne d'un etudiant
  public Double getStudentAverage(Long studentId) {
    OptionalDouble average = gradeRepository
      .findByStudentId(studentId)
      .stream()
      .mapToDouble(Grade::getScore)
      .average();

    return average.isPresent() ? average.getAsDouble() : null;
  }
}
