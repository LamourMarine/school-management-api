package com.marine.gestionecole.service;

import com.marine.gestionecole.entity.Course;
import com.marine.gestionecole.repository.CourseRepository;
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
class CourseServiceTest {

    @Mock
    private CourseRepository repository;

    @InjectMocks
    private CourseService courseService;

    private Course course1;
    private Course course2;

    @BeforeEach
    void setUp() {
        course1 = new Course();
        course1.setId(1L);
        course1.setTitle("Mathematics");
        course1.setCode("MATH101");
        course1.setTeacher("Dr. Smith");

        course2 = new Course();
        course2.setId(2L);
        course2.setTitle("Physics");
        course2.setCode("PHYS101");
        course2.setTeacher("Dr. Johnson");
    }

    @Test
    void testFindAll() {
        // Arrange
        List<Course> courses = Arrays.asList(course1, course2);
        when(repository.findAll()).thenReturn(courses);

        // Act
        List<Course> result = courseService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Mathematics", result.get(0).getTitle());
        assertEquals("Physics", result.get(1).getTitle());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testFindById_Found() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(course1));

        // Act
        Optional<Course> result = courseService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Mathematics", result.get().getTitle());
        assertEquals("MATH101", result.get().getCode());
        assertEquals("Dr. Smith", result.get().getTeacher());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        // Arrange
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Course> result = courseService.findById(99L);

        // Assert
        assertFalse(result.isPresent());
        verify(repository, times(1)).findById(99L);
    }

    @Test
    void testSave_CreateNewCourse() {
        // Arrange
        Course newCourse = new Course();
        newCourse.setTitle("Chemistry");
        newCourse.setCode("CHEM101");
        newCourse.setTeacher("Dr. Brown");

        Course savedCourse = new Course();
        savedCourse.setId(3L);
        savedCourse.setTitle("Chemistry");
        savedCourse.setCode("CHEM101");
        savedCourse.setTeacher("Dr. Brown");

        when(repository.save(any(Course.class))).thenReturn(savedCourse);

        // Act
        Course result = courseService.save(newCourse);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Chemistry", result.getTitle());
        assertEquals("CHEM101", result.getCode());
        verify(repository, times(1)).save(newCourse);
    }

    @Test
    void testSave_UpdateExistingCourse() {
        // Arrange
        Course existingCourse = new Course();
        existingCourse.setId(1L);
        existingCourse.setTitle("Advanced Mathematics");
        existingCourse.setCode("MATH101");
        existingCourse.setTeacher("Dr. Smith");

        when(repository.save(any(Course.class))).thenReturn(existingCourse);

        // Act
        Course result = courseService.save(existingCourse);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Advanced Mathematics", result.getTitle());
        verify(repository, times(1)).save(existingCourse);
    }

    @Test
    void testDeleteById() {
        // Arrange
        doNothing().when(repository).deleteById(1L);

        // Act
        courseService.deleteById(1L);

        // Assert
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void testCount() {
        // Arrange
        when(repository.count()).thenReturn(8L);

        // Act
        long result = courseService.count();

        // Assert
        assertEquals(8L, result);
        verify(repository, times(1)).count();
    }
}