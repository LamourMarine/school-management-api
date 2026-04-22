package com.marine.gestionecole.controller;

import java.util.List;

import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import com.marine.gestionecole.entity.Course;
import com.marine.gestionecole.entity.Teacher;
import com.marine.gestionecole.service.CourseService;
import com.marine.gestionecole.service.TeacherService;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    final TeacherService teacherService;

    final CourseService courseService;

    TeacherController(TeacherService teacherService, CourseService courseService) {
        this.teacherService = teacherService;
        this.courseService = courseService;
    }

    // GET/api/teachers - récupérer tous les professeurs
    @GetMapping
    public List<Teacher> getAllTeacher() {
        return teacherService.findAll();
    }

    // GET /api/teachers/{id} - Récupérer un professeur par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Teacher> getTeacherById(@PathVariable Long id) {
        Optional<Teacher> teacher = teacherService.findById(id);
        return teacher
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
    }

  // GET /api/teachers/{id}/courses
    @GetMapping("/{id}/courses")
    public ResponseEntity<?> getTeacherCourses(@PathVariable Long id) {
        List<Course> courses = courseService.findByTeacherId(id);
        return ResponseEntity.ok(courses);
    }


    // POST /api/teacher - Créer un professeur
    @PostMapping 
    public ResponseEntity<Teacher> createTeacher(@RequestBody Teacher teacher) {
        Teacher saved = teacherService.save(teacher);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/teacher/{id} - Modifier un professeur
    @PutMapping("/{id}")
    public ResponseEntity<Teacher> updateTeacher(@PathVariable Long id, @RequestBody Teacher teacher) {
        if (!teacherService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        teacher.setId(id);
        Teacher updated = teacherService.save(teacher);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/teacher/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        if (!teacherService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        teacherService.deleteById(id);
        return ResponseEntity.noContent().build();
            
    }

    // public static class TeacherRequest {
    //     private Long userId;
    //     private Long courseId;

    //     public Long getUserId() {
    //         return userId;
    //     }

    //     public void setUserId(Long userId) {
    //         this.userId = userId;
    //     }

    //     public Long getCourseId() {
    //         return courseId;
    //     }

    //     public void setCourseId(Long courseId) {
    //         this.courseId = courseId;
    //     }
    // }

}
