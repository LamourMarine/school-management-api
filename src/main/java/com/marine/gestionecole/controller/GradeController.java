package com.marine.gestionecole.controller;

import com.marine.gestionecole.entity.Course;
import com.marine.gestionecole.entity.Grade;
import com.marine.gestionecole.entity.Student;
import com.marine.gestionecole.entity.Teacher;
import com.marine.gestionecole.entity.User;
import com.marine.gestionecole.service.CourseService;
import com.marine.gestionecole.service.GradeService;
import com.marine.gestionecole.service.StudentService;
import com.marine.gestionecole.service.TeacherService;
import com.marine.gestionecole.service.UserService;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/grades")
public class GradeController {

  private final UserService userService;

  final GradeService gradeService;

  final StudentService studentService;

  final CourseService courseService;

  final TeacherService teacherService;

  GradeController(
    GradeService gradeService,
    StudentService studentService,
    CourseService courseService,
    TeacherService teacherService,
    UserService userService
  ) {
    this.gradeService = gradeService;
    this.studentService = studentService;
    this.courseService = courseService;
    this.teacherService = teacherService;
    this.userService = userService;
  }

  // GET /api/grades - récupérer toutes les notes
  @GetMapping
  public List<Grade> getAllGrades() {
    return gradeService.findAll();
  }

  // GET /api/grades/{id} - Récupérer une note par ID
  @GetMapping("/{id}")
  public ResponseEntity<Grade> getGradeById(@PathVariable Long id) {
    Optional<Grade> grade = gradeService.findById(id);
    return grade
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }

  // POST /api/grades - Créer une nouvelle note
  @PostMapping
  public ResponseEntity<?> createGrade(@RequestBody GradeRequest request) {
    // Vérifier que l'étudiant existe
    Optional<Student> student = studentService.findById(request.getStudentId());
    if (!student.isPresent()) {
      return ResponseEntity.badRequest().body("Étudiant introuvable");
    }

    // Vérifier que le cours existe
    Optional<Course> course = courseService.findById(request.getCourseId());
    if (!course.isPresent()) {
      return ResponseEntity.badRequest().body("Cours introuvable");
    }

    // Créer la note
    Grade grade = new Grade(request.getScore(), student.get(), course.get());
    Grade saved = gradeService.save(grade);
    return ResponseEntity.status(HttpStatus.CREATED).body(saved);
  }

  // PUT /api/grades/{id} - Modifier une note
  @PutMapping("/{id}")
  public ResponseEntity<?> updateGrade(
    @PathVariable Long id,
    @RequestBody GradeRequest request
  ) {
    // Vérifier que la note existe
    if (!gradeService.findById(id).isPresent()) {
      return ResponseEntity.notFound().build();
    }

    // Vérifier que l'étudiant existe
    Optional<Student> student = studentService.findById(request.getStudentId());
    if (!student.isPresent()) {
      return ResponseEntity.badRequest().body("Étudiant introuvable");
    }

    // Vérifier que le cours existe
    Optional<Course> course = courseService.findById(request.getCourseId());
    if (!course.isPresent()) {
      return ResponseEntity.badRequest().body("Cours introuvable");
    }

    // Vérifier que le professeur a les droits
    Authentication authentication =
      SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();

    // Récupérer le teacher lié à cet utilisateur
    Optional<User> user = userService.findByUsername(username);
    Optional<Teacher> teacher = teacherService.findByUserId(user.get().getId());

    // Vérifier que le teacher est bien celui du cours
    if (teacher.isPresent()) {
      if (
        course.get().getTeacher() == null ||
        !course.get().getTeacher().getId().equals(teacher.get().getId())
      ) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
          "Vous n'avez pas les droits sur ce cours"
        );
      }
    }
    // Mettre à jour la note
    Grade grade = new Grade(request.getScore(), student.get(), course.get());
    grade.setId(id);
    Grade updated = gradeService.save(grade);
    return ResponseEntity.ok(updated);
  }

  // DELETE /api/grades/{id} - Supprimer une note
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteGrade(@PathVariable Long id) {
    if (!gradeService.findById(id).isPresent()) {
      return ResponseEntity.notFound().build();
    }

    gradeService.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  // GET /api/grades/count - Compter les notes
  @GetMapping("/count")
  public long countGrades() {
    return gradeService.count();
  }

  // Classe interne pour recevoir les données JSON
  public static class GradeRequest {

    private double score;
    private Long studentId;
    private Long courseId;
    private Long teacherId;

    public double getScore() {
      return score;
    }

    public void setScore(double score) {
      this.score = score;
    }

    public Long getStudentId() {
      return studentId;
    }

    public void setStudentId(Long studentId) {
      this.studentId = studentId;
    }

    public Long getCourseId() {
      return courseId;
    }

    public void setCourseId(Long courseId) {
      this.courseId = courseId;
    }

    public Long getTeacherId() {
      return teacherId;
    }

    public void setTeacherId(Long teacherId) {
      this.teacherId = teacherId;
    }
  }
}
