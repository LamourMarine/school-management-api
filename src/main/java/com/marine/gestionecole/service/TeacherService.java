package com.marine.gestionecole.service;

import com.marine.gestionecole.entity.Teacher;
import com.marine.gestionecole.entity.User;
import com.marine.gestionecole.repository.TeacherRepository;
import com.marine.gestionecole.repository.UserRepository;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class TeacherService {

  final TeacherRepository teacherRepository;
  final PasswordEncoder passwordEncoder;
  final UserRepository userRepository;
  final UserService userService;
  TeacherService(
    TeacherRepository teacherRepository,
    PasswordEncoder passwordEncoder,
    UserRepository userRepository,
    UserService userService
  ) {
    this.teacherRepository = teacherRepository;
    this.passwordEncoder = passwordEncoder;
    this.userRepository = userRepository;
    this.userService = userService;
  }

  public Optional<Teacher> findByUserId(Long userId) {
    return teacherRepository.findByUserId(userId);
  }

  public List<Teacher> findAll() {
    return teacherRepository.findAll();
  }

  public Optional<Teacher> findById(Long id) {
    return teacherRepository.findById(id);
  }

  public String generateRandomPassword(int len) {
    String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    Random rnd = new Random();

    StringBuilder sb = new StringBuilder(len);
    for (int i = 0; i < len; i++) {
      sb.append(AB.charAt(rnd.nextInt(AB.length())));
    }
    return sb.toString();
  }

  @Transactional
  public Teacher createTeacherWithAccount(String username, String email, String firstName, String laStName) {
    String rawPassword = generateRandomPassword(10);

    User newTeacher = userService.registerUser(
      username,
      rawPassword,
      email,
      User.Role.TEACHER
    );

    Teacher createTeacher = new Teacher();
    createTeacher.setUser(newTeacher);
    createTeacher.setFirstName(firstName);
    createTeacher.setLastName(laStName);

    return teacherRepository.save(createTeacher);
  }

  public Teacher save(Teacher teacher) {
    return teacherRepository.save(teacher);
  }

  public void deleteById(Long id) {
    teacherRepository.deleteById(id);
  }

  public long count() {
    return teacherRepository.count();
  }
}
