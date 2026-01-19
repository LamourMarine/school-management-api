package com.marine.gestionecole.service;

import com.marine.gestionecole.entity.Course;
import com.marine.gestionecole.entity.Grade;
import com.marine.gestionecole.entity.Student;
import com.marine.gestionecole.repository.GradeRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GradeServiceTest {

    @Mock
    private GradeRepository repository;

    @InjectMocks
    private GradeService gradeService;

    private Grade grade1;
    private Grade grade2;
    private Student student1;
    private Student student2;
    private Course course1;
    private Course course2;

    @BeforeEach
    void setUp() {
        student1 = new Student();
        student1.setId(1L);
        student1.setFirstName("John");
        student1.setLastName("Doe");

        student2 = new Student();
        student2.setId(2L);
        student2.setFirstName("Jane");
        student2.setLastName("Smith");

        course1 = new Course();
        course1.setId(1L);
        course1.setTitle("Mathematics");
        course1.setCode("MATH101");

        course2 = new Course();
        course2.setId(2L);
        course2.setTitle("Physics");
        course2.setCode("PHYS101");

        grade1 = new Grade();
        grade1.setId(1L);
        grade1.setScore(20);
        grade1.setStudent(null);
        grade1.setCourse(null);

        grade2 = new Grade();
        grade2.setId(null);
        grade2.setScore(0);
        grade2.setStudent(null);
        grade2.setCourse(null);

        grade1 = new Grade();
        grade1.setId(1L);
        grade1.setScore(15.5);
        grade1.setStudent(student1);
        grade1.setCourse(course1);

        grade2 = new Grade();
        grade2.setId(2L);
        grade2.setScore(18.0);
        grade2.setStudent(student2);
        grade2.setCourse(course2);
    }

    @Test
    void testFindAll() {
        // Arrange
        List<Grade> grades = Arrays.asList(grade1, grade2);
        when(repository.findAll()).thenReturn(grades);

        // Act
        List<Grade> result = gradeService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(15.5, result.get(0).getScore());
        assertEquals(18, result.get(1).getScore());
        assertEquals("John", result.get(0).getStudent().getFirstName());
        assertEquals("Jane", result.get(1).getStudent().getFirstName());
        assertEquals("Mathematics", result.get(0).getCourse().getTitle());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testFindById_Found() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(grade1));

        // Act
        Optional<Grade> result = gradeService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(15.5, result.get().getScore());
        assertEquals("John", result.get().getStudent().getFirstName());
        assertEquals("Mathematics", result.get().getCourse().getTitle());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        // Arrange
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Grade> result = gradeService.findById(99L);

        // Assert
        assertFalse(result.isPresent());
        verify(repository, times(1)).findById(99L);
    }

    @Test
    void testSave_CreateNewGrade() {
        // Arrange
        Grade newGrade = new Grade();
        newGrade.setScore(12.0);
        newGrade.setStudent(student1);
        newGrade.setCourse(course2);

        Grade savedGrade = new Grade();
        savedGrade.setId(3L);
        savedGrade.setScore(12.0);
        savedGrade.setStudent(student1);
        savedGrade.setCourse(course2);

        when(repository.save(any(Grade.class))).thenReturn(savedGrade);

        // Act
        Grade result = gradeService.save(newGrade);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(12.0, result.getScore());
        assertEquals("John", result.getStudent().getFirstName());
        assertEquals("Physics", result.getCourse().getTitle());
        verify(repository, times(1)).save(newGrade);
    }

    @Test
    void testSave_UpdateExistingGrade() {
        // Arrange
        Grade existingGrade = new Grade();
        existingGrade.setId(1L);
        existingGrade.setScore(17.0);
        existingGrade.setStudent(student1);
        existingGrade.setCourse(course1);

        when(repository.save(any(Grade.class))).thenReturn(existingGrade);

        // Act
        Grade result = gradeService.save(existingGrade);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(17.0, result.getScore());
        verify(repository, times(1)).save(existingGrade);
    }

    @Test
    void testDeleteById() {
        // Arrange
        doNothing().when(repository).deleteById(1L);

        // Act
        gradeService.deleteById(1L);

        // Assert
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void testCount() {
        // Arrange
        when(repository.count()).thenReturn(10L);

        // Act
        long result = gradeService.count();

        // Assert
        assertEquals(10L, result);
        verify(repository, times(1)).count();
    }

    @Test
    void testFindByStudentId() {
        // Arrange
        List<Grade> gradesForStudent = Arrays.asList(grade1);
        when(repository.findByStudentId(1L)).thenReturn(gradesForStudent);

        // Act
        List<Grade> result = gradeService.findByStudentId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getStudent().getFirstName());
        verify(repository, times(1)).findByStudentId(1L);
    }
}
