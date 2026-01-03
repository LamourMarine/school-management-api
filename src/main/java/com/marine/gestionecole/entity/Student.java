package com.marine.gestionecole.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;;

@Entity
@Table(name = "students")
public class Student {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50)
    @Column(nullable = false)
    private String lastName;
    
    @NotBlank
    @Column(nullable = false)
    private String firstName;
    
    // Constructeur vide (OBLIGATOIRE pour JPA)
    public Student() {
    }
    
    // Constructeur avec paramètres
public Student(String lastName, String firstName) {
        this.lastName = lastName;
        this.firstName = firstName;
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}