package com.marine.gestionecole.controller;

import com.marine.gestionecole.dto.AuthResponse;
import com.marine.gestionecole.dto.LoginRequest;
import com.marine.gestionecole.dto.RefreshTokenRequest;
import com.marine.gestionecole.dto.RegisterRequest;
import com.marine.gestionecole.entity.RefreshToken;
import com.marine.gestionecole.entity.User;
import com.marine.gestionecole.repository.RefreshTokenRepository;
import com.marine.gestionecole.service.JwtService;
import com.marine.gestionecole.service.RefreshTokenService;
import com.marine.gestionecole.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000" })
public class AuthController {

  @Autowired
  private UserService userService;

  @Autowired
  private JwtService jwtService;

  @Autowired
  private RefreshTokenService refreshTokenService;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private RefreshTokenRepository refreshTokenRepository;

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
    try {
      User user = userService.registerUser(
        request.getUsername(),
        request.getPassword(),
        request.getEmail()
      );

      UserDetails userDetails = userService.loadUserByUsername(
        user.getUsername()
      );
      String accessToken = jwtService.generateToken(userDetails);

      // Créer refresh token
      RefreshToken refreshToken = refreshTokenService.createRefreshToken(
        user.getId()
      );
      String refreshTokenString = refreshToken.getToken();

      return ResponseEntity.ok(
        new AuthResponse(
          accessToken,
          refreshTokenString,
          user.getUsername(),
          user.getRole().name()
        )
      );
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    try {
      authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
          request.getUsername(),
          request.getPassword()
        )
      );

      User user = userService
        .findByUsername(request.getUsername())
        .orElseThrow(() -> new RuntimeException("User not found"));

      UserDetails userDetails = userService.loadUserByUsername(
        request.getUsername()
      );

      // Créer le refresh token (retourne l'entité RefreshToken)
      RefreshToken refreshToken = refreshTokenService.createRefreshToken(
        user.getId()
      );

      String accessToken = jwtService.generateToken(userDetails);

      // Extraire le token sous forme de String
      String refreshTokenString = refreshToken.getToken();

      return ResponseEntity.ok(
        new AuthResponse(
          accessToken,
          refreshTokenString,
          user.getUsername(),
          user.getRole().name()
        )
      );
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Invalid credentials");
    }
  }

  @PostMapping("/refresh")
  public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest request) {
    try {
      // 1. Chercher le refresh token en base avec la string
      RefreshToken refreshToken = refreshTokenRepository
        .findByToken(request.getRefreshToken())
        .orElseThrow(() -> new RuntimeException("Refresh token not found"));

      // 2. Vérifier qu'il n'est pas expiré
      RefreshToken validatedToken = refreshTokenService.verifyExpiration(
        refreshToken
      );

      // 3. Récupérer le user associé
      User user = validatedToken.getUser();

      //4. Générer  nouveau access token
      UserDetails userDetails = userService.loadUserByUsername(
        user.getUsername()
      );
      String newAccessToken = jwtService.generateToken(userDetails);

      return ResponseEntity.ok(
        new AuthResponse(
          newAccessToken,
          request.getRefreshToken(),
          user.getUsername(),
          user.getRole().name()
        )
      );
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(
        "Invalid or expired refresh token"
      );
    }
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(@RequestBody RefreshTokenRequest request) {
    try {
      // 1. Récupérer le refresh token
      RefreshToken refreshToken = refreshTokenRepository
        .findByToken(request.getRefreshToken())
        .orElseThrow(() -> new RuntimeException("Refresh token not found"));

      // 2. Récupérer le user
      User user = refreshToken.getUser();

      // 3. Révoquer tous les tokens de cet utilisateur
      refreshTokenService.revokeByUser(user);

      // 4. Retourner succès
      return ResponseEntity.ok("Logged out successfully");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Logout failed");
    }
  }
}
