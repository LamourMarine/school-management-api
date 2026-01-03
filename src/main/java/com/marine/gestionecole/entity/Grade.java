package com.marine.gestionecole.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;



@Entity
@Table(name = "grades")
public class Grade {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Min(0)
    @Max(20)
    private double score;

    // RELATIONS JPA
    @ManyToOne  // Plusieurs notes → 1 étudiant
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne  // Plusieurs notes → 1 cours
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // Constructeur vide (obligatoire pour JPA)
    public Grade() {
    }

    // Constructeur avec tous les paramètres
public Grade(double score, Student student, Course course) {
        this.score = score;
        this.student = student;
        this.course = course;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}