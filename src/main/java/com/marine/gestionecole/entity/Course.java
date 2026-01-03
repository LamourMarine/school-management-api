package com.marine.gestionecole.entity;
import jakarta.persistence.*;

@Entity
@Table(name = "course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String teacher;

    @Column(nullable = false, unique = true)
    private String code;

    // Constructeur vide (obligatoire pour JPA)
    public Course() {
    }

    //Constructeur avec parametres
    public Course(String title, String teacher, String code) {
        this.title = title;
        this.teacher = teacher;
        this.code = code;
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getTeacher() {
        return teacher;
    }
    
    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
}