package com.marine.gestionecole.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marine.gestionecole.dto.StudentResponse;
import com.marine.gestionecole.entity.Course;
import com.marine.gestionecole.entity.Grade;
import com.marine.gestionecole.entity.Student;
import com.marine.gestionecole.service.GradeService;
import com.marine.gestionecole.service.StudentService;
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
class StudentControllerTest {

  @Autowired
  private StudentController studentController;

  private ObjectMapper objectMapper;

  @MockitoBean
  private StudentService studentService;

  @MockitoBean
  private GradeService gradeService;

  private MockMvc mockMvc;

  private Student student;

  private Grade grade;

  private Course course;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(studentController).build();
    objectMapper = new ObjectMapper();

    student = new Student();
    student.setId(1L);
    student.setFirstName("Alice");
    student.setLastName("Dupont");
  }

  @Test
  void getAllStudents_shouldReturn200WithList() throws Exception {
    // GIVEN: on programme le faux service
    StudentResponse s1 = new StudentResponse();
    s1.setId(1L);
    s1.setFirstName("Alice");
    s1.setLastName("Dupond");

    StudentResponse s2 = new StudentResponse();
    s2.setId(2L);
    s2.setFirstName("Bob");
    s2.setLastName("Martin");

    when(studentService.findAll()).thenReturn(List.of(s1, s2));

    //WHEN + THEN:on simule la requete et on verifie
    mockMvc
      .perform(get("/api/students"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(2))
      .andExpect(jsonPath("$[0].firstName").value("Alice"))
      .andExpect(jsonPath("$[1].firstName").value("Bob"));
  }

  @Test
  void getStudentById_shouldReturn200WithOptional() throws Exception {
    when(studentService.findById(1L)).thenReturn(Optional.of(student));

    mockMvc
      .perform(get("/api/students/{id}", 1L))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1L));
  }

  @Test
  void shouldReturnNotFoundForInvalidURL() throws Exception {
    when(studentService.findById(99L)).thenReturn(Optional.empty());

    mockMvc
      .perform(get("/api/students/{id}", 99L))
      .andExpect(status().isNotFound());
  }

  @Test
  void shouldCreateStudent() throws Exception {
    Student savedStudent = new Student();
    savedStudent.setId(1L);
    savedStudent.setFirstName("Alice");
    savedStudent.setLastName("Dupont");

    when(studentService.save(any(Student.class))).thenReturn(savedStudent);

    mockMvc
      .perform(
        post("/api/students")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(savedStudent))
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.firstName").value("Alice"));
  }

  @Test
  void shouldUpdateStudent() throws Exception {
    Student existingStudent = new Student();
    existingStudent.setId(1L);
    existingStudent.setFirstName("Johnny");
    existingStudent.setLastName("Doe");

    when(studentService.findById(1L)).thenReturn(Optional.of(existingStudent));
    when(studentService.save(any(Student.class))).thenReturn(existingStudent);

    mockMvc
      .perform(
        put("/api/students/{id}", 1L)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(existingStudent))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(1L))
      .andExpect(jsonPath("$.firstName").value("Johnny"));
  }

  @Test
  void shouldReturnNotFoundWhenUpdatingNonExistentStudent() throws Exception {
    Student existingStudent = new Student();
    existingStudent.setId(1L);
    existingStudent.setFirstName("Johnny");
    existingStudent.setLastName("Doe");

    when(studentService.findById(99L)).thenReturn(Optional.empty());

    mockMvc
      .perform(
        put("/api/students/{id}", 99L)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(existingStudent))
      )
      .andExpect(status().isNotFound());
  }

  @Test
  void shouldDeleteStudent() throws Exception {
    when(studentService.findById(1L)).thenReturn(Optional.of(student));
    doNothing().when(studentService).deleteById(1L);

    mockMvc
      .perform(delete("/api/students/{id}", 1L))
      .andExpect(status().isNoContent());
  }

  @Test
  void shouldReturnNotFoundWhenDeletingNonExistingStudent() throws Exception {
    when(studentService.findById(99L)).thenReturn(Optional.empty());

    mockMvc
      .perform(delete("/api/students/{id}", 99L))
      .andExpect(status().isNotFound());
  }

  @Test
  void shouldGetStudentGrade() throws Exception {
    course = new Course();
    course.setId(2L);
    course.setTitle("Mathematics");
    course.setCode("MATH01");

    grade = new Grade();
    grade.setScore(12.0);
    grade.setStudent(student);
    grade.setCourse(course);

    when(studentService.findById(1L)).thenReturn(Optional.of(student));
    when(gradeService.findByStudentId(1L)).thenReturn(List.of(grade));

    mockMvc
      .perform(get("/api/students/{id}/grades", 1L))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].score").value(12.0));
  }

  @Test
  void shouldGetStudentAverage() throws Exception {
    course = new Course();
    course.setId(2L);
    course.setTitle("Mathematics");
    course.setCode("MATH01");

    grade = new Grade();
    grade.setScore(12.0);
    grade.setStudent(student);
    grade.setCourse(course);

    when(studentService.findById(1L)).thenReturn(Optional.of(student));
    when(gradeService.findByStudentId(1L)).thenReturn(List.of(grade));

    mockMvc
      .perform(get("/api/students/{id}/average", 1L))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").value(12.0));
  }

  @Test
  void testCount() throws Exception {
    when(studentService.count()).thenReturn(5L);

    mockMvc
      .perform(get("/api/students/count"))
      .andExpect(jsonPath("$").value(5L));
  }
}
