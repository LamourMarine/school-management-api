package com.marine.gestionecole.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.marine.gestionecole.entity.User;
import com.marine.gestionecole.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock
  UserRepository userRepository;

  @Mock
  PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserService userService;

  private User mockUser;

  @BeforeEach
  void setUp() {
    mockUser = new User(
      1L,
      "testuser",
      "encodedPassword",
      "test@test.com",
      User.Role.USER
    );
  }

  @Test
  void loadUserByUsername_shouldReturnUserDetails_whenUserIsValid() {
    // Arrange
    when(userRepository.findByUsername("testuser")).thenReturn(
      Optional.of(mockUser)
    );

    //Act
    UserDetails username = userService.loadUserByUsername("testuser");

    // Assert
    assertNotNull(username);
    assertEquals("testuser", username.getUsername());
  }

  @Test
  void loadUserByUsername_shouldThrowException_whenUserIsNotFound() {
    // Arrange
    when(userRepository.findByUsername("testuser")).thenReturn(
      Optional.empty()
    );

    //Assert
    assertThrows(UsernameNotFoundException.class, () -> {
      userService.loadUserByUsername("testuser");
    });
  }

  @Test
  void registerUser_shouldReturnUser_whenCredentialsAreValid() {
    // Arrange
    when(userRepository.existsByUsername("testuser")).thenReturn(false);
    when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
    when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
    when(userRepository.save(any())).thenReturn(mockUser);

    // Act
    User result = userService.registerUser(
      "testuser",
      "encodedPassword",
      "test@test.com"
    );

    // Assert
    assertNotNull(result);
    assertEquals("testuser", result.getUsername());
    assertEquals("encodedPassword", result.getPassword());
    assertEquals("test@test.com", result.getEmail());
  }

  @Test
  void registerUser_shouldThrowException_whenUsernameAlreadyExists() {
    // Arrange
    when(userRepository.existsByUsername(any())).thenReturn(true);

    // Assert
    assertThrows(RuntimeException.class, () -> {
      userService.registerUser("testuser", "encodedPassword", "test@test.com");
    });
  }

  @Test
  void registerUser_shouldThrowException_whenEmailAlreadyExists() {
    // Arrange
    when(userRepository.existsByEmail(any())).thenReturn(true);

    // Assert
    assertThrows(RuntimeException.class, () -> {
      userService.registerUser("testuser", "encodedPassword", "test@test.com");
    });
  }

  @Test
  void findByUsername_shouldReturnUser_whenUserIsValid() {
    // Arrange
    when(userRepository.findByUsername("testuser")).thenReturn(
      Optional.of(mockUser)
    );

    // Act
    Optional<User> result = userService.findByUsername("testuser");

    // Assert
    assertNotNull(result);
    assertEquals("testuser", result.get().getUsername());
  }

  @Test
  void findByUsername_shouldReturnEmpty_whenUserIsNotFound() {
    // Arrange
    when(userRepository.findByUsername("testuser")).thenReturn(
      Optional.empty()
    );

    // ACt
    Optional<User> result = userService.findByUsername("testuser");

    // Assert
    assertFalse(result.isPresent());
  }
}
