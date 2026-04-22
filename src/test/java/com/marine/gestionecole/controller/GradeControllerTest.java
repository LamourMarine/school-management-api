package com.marine.gestionecole.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marine.gestionecole.controller.GradeController.GradeRequest;
import com.marine.gestionecole.entity.Course;
import com.marine.gestionecole.entity.Grade;
import com.marine.gestionecole.entity.Student;
import com.marine.gestionecole.entity.User;
import com.marine.gestionecole.service.CourseService;
import com.marine.gestionecole.service.GradeService;
import com.marine.gestionecole.service.StudentService;
import com.marine.gestionecole.service.UserService;
import com.marine.gestionecole.service.TeacherService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest
@ActiveProfiles("test")
public class GradeControllerTest {

  @Autowired
  private GradeController gradeController;

  @MockitoBean
  private GradeService gradeService;

  @MockitoBean
  private StudentService studentService;

  @MockitoBean
  private CourseService courseService;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private TeacherService teacherService;

  private MockMvc mockMvc;

  private Grade grade1;
  private Grade grade2;

  private Student student;

  private Course course;

  private ObjectMapper objectMapper;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(gradeController).build();
    objectMapper = new ObjectMapper();

    student = new Student();
    student.setId(2L);
    student.setFirstName("Alice");
    student.setLastName("Dupont");

    course = new Course();
    course.setId(5L);
    course.setTitle("Mathematics");
    course.setCode("MATH01");

    grade1 = new Grade();
    grade1.setId(1L);
    grade1.setScore(12.0);
    grade1.setStudent(student);
    grade1.setCourse(course);

    grade2 = new Grade();
    grade2.setId(3L);
    grade2.setScore(18.0);
    grade2.setStudent(student);
    grade2.setCourse(course);
  }

  @Test
  void getAllGrades_shouldReturn200WithList() throws Exception {
    when(gradeService.findAll()).thenReturn(List.of(grade1, grade2));

    mockMvc
      .perform(get("/api/grades"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  void getGradeById_shouldReturn200WithOptional() throws Exception {
    when(gradeService.findById(1L)).thenReturn(Optional.of(grade1));

    mockMvc
      .perform(get("/api/grades/{id}", 1L))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1L));
  }

  @Test
  void shouldReturnNotFoundForInvalidURL() throws Exception {
    when(gradeService.findById(99L)).thenReturn(Optional.empty());

    mockMvc
      .perform(get("/api/grades/{id}", 99L))
      .andExpect(status().isNotFound());
  }

  @Test
  void shouldCreateGrade() throws Exception {
    GradeRequest g1 = new GradeRequest();
    g1.setCourseId(5L);
    g1.setScore(13.0);
    g1.setStudentId(2L);

    Grade savedGrade = new Grade();
    savedGrade.setId(8L);
    savedGrade.setCourse(course);
    savedGrade.setScore(13.0);
    savedGrade.setStudent(student);

    when(studentService.findById(2L)).thenReturn(Optional.of(student));
    when(courseService.findById(5L)).thenReturn(Optional.of(course));
    when(gradeService.save(any(Grade.class))).thenReturn((savedGrade));

    mockMvc
      .perform(
        post("/api/grades")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(g1))
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.student.firstName").value("Alice"))
      .andExpect(jsonPath("$.student.lastName").value("Dupont"))
      .andExpect(jsonPath("$.score").value(13.0))
      .andExpect(jsonPath("$.course.title").value("Mathematics"));
  }

@Test
@WithMockUser(username = "testuser", roles = {"ADMIN"})
void shouldUpdateGrade() throws Exception {
    GradeRequest g1 = new GradeRequest();
    g1.setCourseId(5L);
    g1.setScore(13.0);
    g1.setStudentId(2L);

    Grade existingGrade = new Grade();
    existingGrade.setId(1L);
    existingGrade.setCourse(course);
    existingGrade.setScore(13.0);
    existingGrade.setStudent(student);

    User user = new User();
    user.setId(1L);
    user.setUsername("testuser");

    when(gradeService.findById(1L)).thenReturn(Optional.of(existingGrade));
    when(studentService.findById(2L)).thenReturn(Optional.of(student));
    when(courseService.findById(5L)).thenReturn(Optional.of(course));
    when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
    when(teacherService.findByUserId(1L)).thenReturn(Optional.empty());
    when(gradeService.save(any(Grade.class))).thenReturn(existingGrade);

    mockMvc
      .perform(
        put("/api/grades/{id}", 1L)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(g1))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.score").value(13.0));
}
  @Test
  void shouldDeleteGrade() throws Exception {
    when(gradeService.findById(1L)).thenReturn((Optional.of(grade1)));
    doNothing().when(gradeService).deleteById(1L);

    mockMvc
      .perform(delete("/api/grades/{id}", 1L))
      .andExpect((status().isNoContent()));
  }

  @Test
  void shouldReturnNotFoundWhenDeletingNotExistingGrade() throws Exception {
    when(gradeService.findById(99L)).thenReturn(Optional.empty());

    mockMvc
      .perform(delete("/api/grades/{id}", 99L))
      .andExpect(status().isNotFound());
  }

  @Test
  void shouldCountGrades() throws Exception {
    when(gradeService.count()).thenReturn(5L);

    mockMvc
      .perform(get("/api/grades/count"))
      .andExpect(jsonPath("$").value(5L));
  }
}
