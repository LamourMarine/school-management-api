package com.marine.gestionecole.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

  @Mock
  private UserDetails mockUserDetails;

  private JwtService jwtService;

  @BeforeEach
  void setUp() {
    jwtService = new JwtService();
    ReflectionTestUtils.setField(
      jwtService,
      "secretKey",
      "test-secret-key-must-be-long-enough"
    );
    ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86400000L);
  }

  @Test
  void extractUsername_shouldReturnUsername_whenTokenIsValid() {
    // Arrange (générer un vrai token)
    when(mockUserDetails.getUsername()).thenReturn("testuser");
    String token = jwtService.generateToken(mockUserDetails);

    //Act
    String username = jwtService.extractUsername(token);

    //Assert
    assertEquals("testuser", username);
  }

  @Test
  void extractUsername_shouldThrowException_whenTokenIsInvalid() {
    assertThrows(Exception.class, () -> {
      jwtService.extractUsername("invalid-token");
    });
  }

  @Test
  void extractClaim_shouldReturnSubject_whenTokenIsValid() {
    // Arrange
    when(mockUserDetails.getUsername()).thenReturn("testuser");
    String token = jwtService.generateToken(mockUserDetails);

    // Act
    String username = jwtService.extractClaim(token, Claims::getSubject);

    //Assert
    assertEquals("testuser", username);
  }

  @Test
  void extractClaim_shouldThrowException_whenTokenIsInvalid() {
    assertThrows(Exception.class, () -> {
      jwtService.extractClaim("invalid-token", Claims::getSubject);
    });
  }

  @Test
  void isTokenValid_shouldReturnTrue_whenTokenIsValid() {
    // Arrange
    when(mockUserDetails.getUsername()).thenReturn("testuser");
    String token = jwtService.generateToken(mockUserDetails);

    // Act
    Boolean isValid = jwtService.isTokenValid(token, mockUserDetails);

    //Assert
    assertTrue(isValid);
  }

  @Test
  void isTokenValid_shouldReturnFalse_whenUsernameDoesNotMatch() {
    // Arrange
    when(mockUserDetails.getUsername()).thenReturn("testuser");
    String token = jwtService.generateToken(mockUserDetails);

    UserDetails otherUserDetails = mock(UserDetails.class);
    when(otherUserDetails.getUsername()).thenReturn("autreuser");

    // Act
    Boolean isValid = jwtService.isTokenValid(token, otherUserDetails);

    // Assert
    assertFalse(isValid);
  }
}
