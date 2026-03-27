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
import com.marine.gestionecole.entity.Course;
import com.marine.gestionecole.entity.Grade;
import com.marine.gestionecole.entity.Student;
import com.marine.gestionecole.service.CourseService;
import com.marine.gestionecole.service.GradeService;
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
public class CourseControllerTest {

  @Autowired
  private CourseController courseController;

  @MockitoBean
  private CourseService courseService;

  @MockitoBean
  private GradeService gradeService;

  private MockMvc mockMvc;

  private Course course1;
  private Course course2;

  private Grade grade;

  private Student student;

  private ObjectMapper objectMapper;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();
    objectMapper = new ObjectMapper();

    course1 = new Course();
    course1.setId(5L);
    course1.setTitle("Mathematics");
    course1.setCode("MATH01");

    course2 = new Course();
    course2.setId(10L);
    course2.setTitle("History");
    course2.setCode("HIST02");

    student = new Student();
    student.setId(1L);
    student.setFirstName("Alice");
    student.setLastName("Dupont");

    grade = new Grade();
    grade.setScore(12.0);
    grade.setStudent(student);
    grade.setCourse(course1);
  }

  @Test
  void getAllCourse_shouldReturn200WithList() throws Exception {
    when(courseService.findAll()).thenReturn(List.of(course1, course2));

    mockMvc
      .perform(get("/api/courses"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()").value(2))
      .andExpect(jsonPath("$[0].title").value("Mathematics"))
      .andExpect(jsonPath("$[1].title").value("History"));
  }

  @Test
  void getCourseById_shouldReturn200WithOptional() throws Exception {
    when(courseService.findById(5L)).thenReturn(Optional.of(course1));

    mockMvc
      .perform(get("/api/courses/{id}", 5L))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(5L));
  }

  @Test
  void shouldReturnNotFoundForInvalidURL() throws Exception {
    when(courseService.findById(99L)).thenReturn(Optional.empty());

    mockMvc
      .perform(get("/api/courses/{id}", 99l))
      .andExpect(status().isNotFound());
  }

  @Test
  void shouldCreateCourse() throws Exception {
    Course savedCourse = new Course();
    savedCourse.setId(15L);
    savedCourse.setTitle("French");
    savedCourse.setCode("FRENCH03");

    when(courseService.save(any(Course.class))).thenReturn(savedCourse);

    mockMvc
      .perform(
        post("/api/courses")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(savedCourse))
      )
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.code").value("FRENCH03"));
  }

  @Test
  void shouldUpdateCourse() throws Exception {
    Course existingCourse = new Course();
    existingCourse.setId(5L);
    existingCourse.setTitle("Mathematics");
    existingCourse.setCode("MATH01");

    when(courseService.findById(5L)).thenReturn(Optional.of(existingCourse));
    when(courseService.save(any(Course.class))).thenReturn(existingCourse);

    mockMvc
      .perform(
        put("/api/courses/{id}", 5L)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(existingCourse))
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id").value(5L))
      .andExpect(jsonPath("$.title").value("Mathematics"));
  }

  @Test
  void getCourseGrades_shouldReturn200WithList() throws Exception {
    when(courseService.findById(5L)).thenReturn(Optional.of(course1));
    when(gradeService.findByCourseId(5L)).thenReturn(List.of(grade));

    mockMvc
      .perform(get("/api/courses/{id}/grades", 5L))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].score").value(12.0));
  }

  @Test
  void shouldDeleteCourse() throws Exception {
    when(courseService.findById(5L)).thenReturn(Optional.of(course1));
    doNothing().when(courseService).deleteById(5L);

    mockMvc
      .perform(delete("/api/courses/{id}", 5L))
      .andExpect(status().isNoContent());
  }

  @Test
  void shouldReturnNotFoundWhenDeletingNotExistingCourse() throws Exception {
    when(courseService.findById(99L)).thenReturn(Optional.empty());

    mockMvc
      .perform(delete("/api/courses/{id}", 99L))
      .andExpect(status().isNotFound());
  }

  @Test
  void shouldCountCourses() throws Exception {
    when(courseService.count()).thenReturn(5L);

    mockMvc
      .perform(get("/api/courses/count"))
      .andExpect(jsonPath("$").value(5L));
  }
}
