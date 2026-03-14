package com.marine.gestionecole.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "grades")
@Setter
@Getter
@NoArgsConstructor
public class Grade {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  @Min(0)
  @Max(20)
  private double score;

  // RELATIONS JPA
  @ManyToOne // Plusieurs notes → 1 étudiant
  @JoinColumn(name = "student_id", nullable = false)
  private Student student;

  @ManyToOne // Plusieurs notes → 1 cours
  @JoinColumn(name = "course_id", nullable = false)
  private Course course;

  // Constructeur avec tous les paramètres
  public Grade(double score, Student student, Course course) {
    this.score = score;
    this.student = student;
    this.course = course;
  }
}
