package com.marine.gestionecole.controller;

import com.marine.gestionecole.entity.Student;
import com.marine.gestionecole.entity.Grade;
import com.marine.gestionecole.service.StudentService;
import com.marine.gestionecole.service.GradeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    
    @Autowired
    private StudentService service;

    @Autowired
    private GradeService gradeService;
    
    
    // GET /api/students - Récupérer tous les étudiants
    @GetMapping
    public List<Student> getAllStudents() {
        return service.findAll();
    }
    
    // GET /api/students/{id} - Récupérer un étudiant par ID
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        Optional<Student> student = service.findById(id);
        return student.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    // POST /api/students - Créer un nouvel étudiant
    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        Student saved = service.save(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // GET /api/students/{id}/grades - Récupérer toutes les notes d'un étudiant
    @GetMapping("/{id}/grades")
    public ResponseEntity<?> getStudentGrades(@PathVariable Long id) {
        Optional<Student> student = service.findById(id);
        if(!student.isPresent()) {
            ResponseEntity.notFound().build();
        }
        List<Grade>grades = gradeService.findByStudentId(id);
        return ResponseEntity.ok(grades);
    }

    //GET /api/students/{id}/average - Retourner la moyenne d'un étudiant
    @GetMapping("/{id}/average")
    public ResponseEntity<?> getStudentAverage(@PathVariable Long id) {

        // Vérifier que l'étudiant existe
        if (!service.findById(id).isPresent()) {
          return ResponseEntity.notFound().build();
        }
        //Récupérer toutes les notes
        List<Grade> grades = gradeService.findByStudentId(id);
        //Calculer la moyenne
        if (grades.isEmpty()) {
            return ResponseEntity.ok(0.0);
        }

        double sum = 0;
        for (Grade grade : grades) {
            sum += grade.getScore();
        }

        double average = sum / grades.size();
        return ResponseEntity.ok(average);
     }


    
    // PUT /api/students/{id} - Modifier un étudiant
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(
            @PathVariable Long id, 
            @RequestBody Student student) {
        
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        student.setId(id);
        Student updated = service.save(student);
        return ResponseEntity.ok(updated);
    }
    
    // DELETE /api/students/{id} - Supprimer un étudiant
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    // GET /api/students/count - Compter les étudiants
    @GetMapping("/count")
    public long countStudents() {
        return service.count();
    }

    //Classe interne pour recevoir les données en JSON
    public static class StudentRequest {
        private String lastName;
        private String firstName;
        private Long studentId;

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

        public Long getStudentId() {
        return studentId;
        }

        public void setStudentId(Long studentId) {
        this.studentId = studentId;
        }

    }

}
