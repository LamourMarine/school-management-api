package com.marine.gestionecole.service;

import com.marine.gestionecole.entity.Student;
import com.marine.gestionecole.repository.StudentRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository repository;

    @InjectMocks
    private StudentService studentService;

    private Student student1;
    private Student student2;

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
    }

    @Test
    void testFindAll() {
        // Arrange
        List<Student> students = Arrays.asList(student1, student2);
        when(repository.findAll()).thenReturn(students);

        // Act
        List<Student> result = studentService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Jane", result.get(1).getFirstName());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testFindById_Found() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(student1));

        // Act
        Optional<Student> result = studentService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
        assertEquals("Doe", result.get().getLastName());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        // Arrange
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Student> result = studentService.findById(99L);

        // Assert
        assertFalse(result.isPresent());
        verify(repository, times(1)).findById(99L);
    }

    @Test
    void testSave_CreateNewStudent() {
        // Arrange
        Student newStudent = new Student();
        newStudent.setFirstName("Alice");
        newStudent.setLastName("Johnson");

        Student savedStudent = new Student();
        savedStudent.setId(3L);
        savedStudent.setFirstName("Alice");
        savedStudent.setLastName("Johnson");

        when(repository.save(any(Student.class))).thenReturn(savedStudent);

        // Act
        Student result = studentService.save(newStudent);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Alice", result.getFirstName());
        assertEquals("Johnson", result.getLastName());
        verify(repository, times(1)).save(newStudent);
    }

    @Test
    void testSave_UpdateExistingStudent() {
        // Arrange
        Student existingStudent = new Student();
        existingStudent.setId(1L);
        existingStudent.setFirstName("Johnny");
        existingStudent.setLastName("Doe");

        when(repository.save(any(Student.class))).thenReturn(existingStudent);

        // Act
        Student result = studentService.save(existingStudent);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Johnny", result.getFirstName());
        verify(repository, times(1)).save(existingStudent);
    }

    @Test
    void testDeleteById() {
        // Arrange
        doNothing().when(repository).deleteById(1L);

        // Act
        studentService.deleteById(1L);

        // Assert
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void testCount() {
        // Arrange
        when(repository.count()).thenReturn(5L);

        // Act
        long result = studentService.count();

        // Assert
        assertEquals(5L, result);
        verify(repository, times(1)).count();
    }
}