package com.marine.gestionecole.controller;

import com.marine.gestionecole.dto.AuthResponse;
import com.marine.gestionecole.dto.LoginRequest;
import com.marine.gestionecole.dto.RegisterRequest;
import com.marine.gestionecole.service.AuthenticationService;
import com.marine.gestionecole.service.CookieService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000" })
public class AuthController {

  @Autowired
  private CookieService cookieService;

  @Autowired
  private AuthenticationService authenticationService;

  @PostMapping("/register")
  public ResponseEntity<?> register(
    @RequestBody RegisterRequest request,
    HttpServletResponse response
  ) {
    try {
      AuthResponse authResponse = authenticationService.register(request);
      //Gérer le cookie
      cookieService.addRefreshTokenCookie(
        response,
        authResponse.getRefreshToken()
      );
      //Nettoyer l'AuthResponse (enlever le refresh token)
      authResponse.setRefreshToken(null);

      return ResponseEntity.ok(authResponse);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(
    @RequestBody LoginRequest request,
    HttpServletResponse response
  ) {
    try {
      AuthResponse authResponse = authenticationService.login(request);
      String refreshToken = authResponse.getRefreshToken();

      cookieService.addRefreshTokenCookie(response, refreshToken);

      // Enlever le refresh token de la réponse (sécurité)
      authResponse.setRefreshToken(null);

      // Retourner au frontend
      return ResponseEntity.ok(authResponse);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Invalid credentials");
    }
  }

  @PostMapping("/refresh")
  public ResponseEntity<?> refresh(
    HttpServletRequest request,
    HttpServletResponse response
  ) {
    try {
      // 1. Extraire le refresh token du cookie
      String refreshToken = cookieService.getRefreshTokenFromCookie(request);
      System.out.println("Refresh token reçu : " + refreshToken);

      if (refreshToken == null) {
        return ResponseEntity.badRequest().body("Refresh token not found");
      }

      // 2. Appeler le service
      AuthResponse authResponse = authenticationService.refresh(refreshToken);

      // 3. Mettre le nouveau token dans un cookie
      cookieService.addRefreshTokenCookie(
        response,
        authResponse.getRefreshToken()
      );

      // 4. Nettoyer
      authResponse.setRefreshToken(null);

      return ResponseEntity.ok(authResponse);
    } catch (Exception e) {
      System.out.println("Erreur refresh : " + e.getMessage());
    e.printStackTrace();
      return ResponseEntity.badRequest().body(
        "Invalid or expired refresh token"
      );
    }
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(
    HttpServletRequest request,
    HttpServletResponse response
  ) {
    try {
      // 1. Extraire le refresh token du cookie
      String refreshToken = cookieService.getRefreshTokenFromCookie(request);

      if (refreshToken != null) {
        // 2. Révoquer le token
        authenticationService.logout(refreshToken);
      }

      // 3. Supprimer le cookie
      cookieService.deleteRefreshTokenCookie(response);

      return ResponseEntity.ok("Logged out successfully");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Logout failed");
    }
  }
}
