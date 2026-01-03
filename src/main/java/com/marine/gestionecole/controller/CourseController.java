package com.marine.gestionecole.controller;

import com.marine.gestionecole.entity.Course;
import com.marine.gestionecole.entity.Grade;
import com.marine.gestionecole.service.CourseService;
import com.marine.gestionecole.service.GradeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cours")
public class CourseController {

    @Autowired
    private CourseService service;

    @Autowired
    private GradeService gradeService;

    // Get /api/course - récupérer tous les cours
    @GetMapping 
    public List<Course> getAllCourse() {
        return service.findAll();
    }
    
    // Get /api/course/{id} - Récupérer un cours par ID
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        Optional<Course> course = service.findById(id);
        return course.map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/course - Créer un nouveau cours
    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        Course saved = service.save(course);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/course/{id} - modifier un cours
    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(
        @PathVariable Long id,
        @RequestBody Course course) {
            
    if (!service.findById(id).isPresent()) {
        return ResponseEntity.notFound().build();
    }

    course.setId(id);
    Course updated = service.save(course);
    return ResponseEntity.ok(updated);
        }

    // GET /api/course/{id}/grades - Récupérer toutes les notes d'un cours
    @GetMapping("/{id}/grades")
    public ResponseEntity<?> getNotesCourse(@PathVariable Long id) {
        // 1. Vérifier que le cours existe
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        // 2. Récupérer les notes avec noteService.findByCoursId(id)
        List<Grade> grades = gradeService.findByCourseId(id);
        // 3. Retourner les notes
        return ResponseEntity.ok(grades);
    }


    // DELETE /api/course/{id} - Supprimer un cours
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/course/count - Compter les cours
    @GetMapping("/count")
    public long countCours() {
        return service.count();
    }
}
