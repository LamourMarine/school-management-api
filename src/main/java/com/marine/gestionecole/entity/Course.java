package com.marine.gestionecole.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "courses")
@Setter
@Getter
@NoArgsConstructor
public class Course {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  private String teacher;

  @Column(nullable = false, unique = true)
  private String code;
}
