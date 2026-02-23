package com.marine.gestionecole.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.marine.gestionecole.dto.AuthResponse;
import com.marine.gestionecole.dto.LoginRequest;
import com.marine.gestionecole.dto.RegisterRequest;
import com.marine.gestionecole.entity.RefreshToken;
import com.marine.gestionecole.entity.User;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

  @Mock
  private UserService userService;

  @Mock
  private JwtService jwtService;

  @Mock
  private RefreshTokenService refreshTokenService;

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private UserDetails mockUserDetails;

  @InjectMocks
  private AuthenticationService authenticationService;

  private User mockUser;
  private RegisterRequest registerRequest;
  private RefreshToken mockRefreshToken;

  private LoginRequest loginRequest;

  @BeforeEach
  void setUp() {
    registerRequest = new RegisterRequest();
    registerRequest.setUsername("testuser");
    registerRequest.setPassword("testpassword");
    registerRequest.setEmail("test@test.com");

    mockUser = new User(
      1L,
      "testuser",
      "encodedPassword",
      "test@test.com",
      User.Role.USER
    );

    mockRefreshToken = new RefreshToken();
    mockRefreshToken.setToken("fake-refresh-token");
    mockRefreshToken.setUser(mockUser);

    loginRequest = new LoginRequest();
    loginRequest.setUsername("testuser");
    loginRequest.setPassword("testpassword");
  }

  @Test
  void register_shouldReturnAuthResponse_whenUserIsValid() {
    // Arrange
    when(userService.registerUser(any(), any(), any())).thenReturn(mockUser);
    when(userService.loadUserByUsername(any())).thenReturn(mockUserDetails);
    when(jwtService.generateToken(any())).thenReturn("fake-access-token");
    when(refreshTokenService.createRefreshToken(any())).thenReturn(
      mockRefreshToken
    );

    // Act
    AuthResponse result = authenticationService.register(registerRequest);

    // Assert
    assertNotNull(result);
    assertEquals("testuser", result.getUsername());
  }

  @Test
  void register_shouldThrowException_whenUsernameAlreadyExist() {
    // Arrange
    when(userService.registerUser(any(), any(), any())).thenThrow(
      new RuntimeException("Username already exists")
    );

    // Assert
    assertThrows(RuntimeException.class, () -> {
      authenticationService.register(registerRequest);
    });
  }

  @Test
  void login_shouldReturnAuthResponse_whenUserIsLogin() {
    // Arrange
    when(userService.findByUsername(any())).thenReturn(Optional.of(mockUser));
    when(userService.loadUserByUsername(any())).thenReturn(mockUserDetails);
    when(refreshTokenService.createRefreshToken(any())).thenReturn(
      mockRefreshToken
    );
    when(jwtService.generateToken(any())).thenReturn("fake-access-token");

    // Act
    AuthResponse result = authenticationService.login(loginRequest);

    // Assert
    assertNotNull(result);
    assertEquals("testuser", result.getUsername());
  }

  @Test
  void login_shouldThrowsException_whenUsernameIsNotExisting() {
    // Arrange
    when(authenticationManager.authenticate(any())).thenThrow(
      new BadCredentialsException("Bad credentials")
    );

    // Assert
    assertThrows(BadCredentialsException.class, () -> {
      authenticationService.login(loginRequest);
    });
  }

  @Test
  void refresh_shouldReturnAuthResponse_whenRefreshTokenIsCreate() {
    // Arrange
    when(refreshTokenService.findByToken(any())).thenReturn(mockRefreshToken);
    when(refreshTokenService.verifyExpiration(any())).thenReturn(
      mockRefreshToken
    );
    when(userService.loadUserByUsername(any())).thenReturn(mockUserDetails);
    when(jwtService.generateToken(any())).thenReturn("fake-access-token");
    when(refreshTokenService.createRefreshToken(any())).thenReturn(
      mockRefreshToken
    );

    //Act
    AuthResponse result = authenticationService.refresh("fake-refresh-token");

    //Assert
    assertNotNull(result);
    assertEquals("testuser", result.getUsername());
    assertEquals("fake-refresh-token", result.getRefreshToken());
  }

  @Test
  void refresh_shouldThrowsException_whenRefreshTokenIsNotExisting() {
    //Arrange
    when(refreshTokenService.findByToken(any())).thenThrow(
      new RuntimeException("Refresh token not found")
    );

    // Assert
    assertThrows(RuntimeException.class, () -> {
        authenticationService.refresh("fake-refresh-token");
    });
  }

  @Test
  void  refresh_shouldThrowsException_whenRefreshTokenIsExpired() {

    //Arrange
    when(refreshTokenService.findByToken(any())).thenReturn(mockRefreshToken);
    when(refreshTokenService.verifyExpiration(any())).thenThrow(
      new RuntimeException("Refresh token was expired. Please login again")
    );

    //Assert
    assertThrows(RuntimeException.class, () -> {
        authenticationService.refresh("fake-refresh-token");
    });

  }

  @Test
  void logout_shouldLogout(){
    // Arrange
    when(refreshTokenService.findByToken(any())).thenReturn(mockRefreshToken);
    doNothing().when(refreshTokenService).revokeByUser(mockUser);

    // Act
    authenticationService.logout("fake-refresh-token");

    // Assert
    verify(refreshTokenService, times(1)).revokeByUser(mockUser);
  }
}
