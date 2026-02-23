package com.marine.gestionecole.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.marine.gestionecole.entity.RefreshToken;
import com.marine.gestionecole.entity.User;
import com.marine.gestionecole.repository.RefreshTokenRepository;
import com.marine.gestionecole.repository.UserRepository;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private RefreshTokenRepository refreshTokenRepository;

  @InjectMocks
  private RefreshTokenService refreshTokenService;

  private RefreshToken mockRefreshToken;
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

    ReflectionTestUtils.setField(refreshTokenService, "refreshTokenDurationMs", 604800000L);

    mockRefreshToken = new RefreshToken();
    mockRefreshToken.setId(1L);
    mockRefreshToken.setToken("fake-refresh-token");
    mockRefreshToken.setUser(mockUser);
    mockRefreshToken.setExpiryDate(Instant.now().plusSeconds(3600));
    mockRefreshToken.setCreatedAt(Instant.now());
    mockRefreshToken.setRevoked(false);
  }

  @Test
  void createRefreshToken_shouldReturnRefreshToken_whenUserExists() {
    // Arrange
    when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
    when(refreshTokenRepository.save(any())).thenReturn(mockRefreshToken);

    // Act
    RefreshToken result = refreshTokenService.createRefreshToken(1L);

    // Assert
    assertNotNull(result);
    assertEquals("fake-refresh-token", result.getToken());
  }

  @Test
  void createRefreshToken_shouldThrowException_whenUserIdIsNotFound() {
    // Arrange
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    //Assert
    assertThrows(RuntimeException.class, () -> {
      refreshTokenService.createRefreshToken(1L);
    });
  }

  @Test
  void findByToken_shouldReturnRefreshToken_whenTokenExist() {
    // Arrange
    when(refreshTokenRepository.findByToken(any())).thenReturn(
      Optional.of(mockRefreshToken)
    );

    // Act
    RefreshToken result = refreshTokenService.findByToken("fake-refresh-token");

    // Assert
    assertNotNull(result);
    assertEquals("fake-refresh-token", result.getToken());
  }

  @Test
  void findByToken_shouldThrowException_whenRefreshTokenIsNotFound() {
    //Arrange
    when(refreshTokenRepository.findByToken(any())).thenReturn(
      Optional.empty()
    );

    // Assert
    assertThrows(RuntimeException.class, () -> {
      refreshTokenService.findByToken(any());
    });
  }

  @Test
  void verifyExpiration_shouldReturnRefreshToken_whenDateIsNotExpired() {
    // Act
    RefreshToken result = refreshTokenService.verifyExpiration(
      mockRefreshToken
    );

    // Assert
    assertNotNull(result);
  }

  @Test
  void verifyExpiration_shouldThrowException_whenTokenIsExpired() {
    // Arrange
    mockRefreshToken.setExpiryDate(Instant.now().minusSeconds(3600));
    doNothing().when(refreshTokenRepository).delete(mockRefreshToken);

    //Assert
    assertThrows(RuntimeException.class, () -> {
      refreshTokenService.verifyExpiration(mockRefreshToken);
    });
  }

  @Test
  void revokeByUser_shouldRevokeRefreshToken_whenUserExists() {
    // Arrange
    doNothing().when(refreshTokenRepository).revokeAllByUser(mockUser);

    // Act
    refreshTokenService.revokeByUser(mockUser);

    // Assert
    verify(refreshTokenRepository, times(1)).revokeAllByUser(mockUser);
  }

  @Test
  void revokeToken_shouldRevokeRefreshToken() {
    // Arrange
    doNothing().when(refreshTokenRepository).revokeByToken("fake-refresh-token");

    // Act
    refreshTokenService.revokeToken("fake-refresh-token");

    // Assert
    verify(refreshTokenRepository, times(1)).revokeByToken("fake-refresh-token");
  }
}
