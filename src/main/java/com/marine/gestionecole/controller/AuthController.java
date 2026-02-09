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
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
  public ResponseEntity<?> register(
    @RequestBody RegisterRequest request,
    HttpServletResponse response
  ) {
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

      // Créer le cookie httpOnly pour le refresh token
      Cookie refreshTokenCookie = new Cookie(
        "refreshToken",
        refreshTokenString
      );
      refreshTokenCookie.setHttpOnly(true);
      refreshTokenCookie.setSecure(false); // true en production avec HTTPS
      refreshTokenCookie.setPath("/api/auth");
      refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7 jours en secondes
      refreshTokenCookie.setAttribute("SameSite", "Lax"); // Protection CSRF
      response.addCookie(refreshTokenCookie);

      return ResponseEntity.ok(
        new AuthResponse(
          accessToken,
          null,
          user.getUsername(),
          user.getRole().name()
        )
      );
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

      // Créer le cookie httpOnly pour le refresh token
      Cookie refreshTokenCookie = new Cookie(
        "refreshToken",
        refreshTokenString
      );
      refreshTokenCookie.setHttpOnly(true);
      refreshTokenCookie.setSecure(false); // true en production avec HTTPS
      refreshTokenCookie.setPath("/api/auth");
      refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7 jours en secondes
      refreshTokenCookie.setAttribute("SameSite", "Lax"); // Protection CSRF
      response.addCookie(refreshTokenCookie);

      return ResponseEntity.ok(
        new AuthResponse(
          accessToken,
          null,
          user.getUsername(),
          user.getRole().name()
        )
      );
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
      // Récupérer le refresh token depuis le cookie
      Cookie[] cookies = request.getCookies();
      String refreshTokenString = null;

      if (cookies != null) {
        for (Cookie cookie : cookies) {
          if ("refreshToken".equals(cookie.getName())) {
            refreshTokenString = cookie.getValue();
            break;
          }
        }
      }

      if (refreshTokenString == null) {
        return ResponseEntity.badRequest().body("Refresh token not found");
      }

      // Chercher le refresh token en base avec la string
      RefreshToken refreshToken = refreshTokenRepository
        .findByToken(refreshTokenString)
        .orElseThrow(() -> new RuntimeException("Refresh token not found"));

      // Vérifier qu'il n'est pas expiré
      RefreshToken validatedToken = refreshTokenService.verifyExpiration(
        refreshToken
      );

      // Récupérer le user associé
      User user = validatedToken.getUser();

      // Générer  nouveau access token
      UserDetails userDetails = userService.loadUserByUsername(
        user.getUsername()
      );
      String newAccessToken = jwtService.generateToken(userDetails);

      // Créer un nouveau refresh token
      RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(
        user.getId()
      );

      // Révoquer l'ancien
      refreshTokenService.revokeByUser(user);

      // Mettre à jour le cookie avec le nouveau refresh token
      Cookie newRefreshTokenCookie = new Cookie(
        "refreshToken",
        newRefreshToken.getToken()
      );
      newRefreshTokenCookie.setHttpOnly(true);
      newRefreshTokenCookie.setSecure(false);
      newRefreshTokenCookie.setPath("/api/auth");
      newRefreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);
      newRefreshTokenCookie.setAttribute("SameSite", "Lax");
      response.addCookie(newRefreshTokenCookie);

      return ResponseEntity.ok(
        new AuthResponse(
          newAccessToken,
          null, // Pas besoin de retourner le refresh token
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
  public ResponseEntity<?> logout(
    HttpServletRequest request,
    HttpServletResponse response
  ) {
    try {
      // Récupérer le refresh token depuis le cookie
      Cookie[] cookies = request.getCookies();
      String refreshTokenString = null;

      if (cookies != null) {
        for (Cookie cookie : cookies) {
          if ("refreshToken".equals(cookie.getName())) {
            refreshTokenString = cookie.getValue();
            break;
          }
        }
      }

      if (refreshTokenString != null) {
        // Récupérer le refresh token et le user
        RefreshToken refreshToken = refreshTokenRepository
          .findByToken(refreshTokenString)
          .orElse(null);

        if (refreshToken != null) {
          User user = refreshToken.getUser();
          // Révoquer tous les tokens de cet utilisateur
          refreshTokenService.revokeByUser(user);
        }
      }

      // Supprimer le cookie côté client
      Cookie deleteCookie = new Cookie("refreshToken", null);
      deleteCookie.setPath("/api/auth");
      deleteCookie.setHttpOnly(true);
      deleteCookie.setMaxAge(0); //  Supprime le cookie
      response.addCookie(deleteCookie);

      return ResponseEntity.ok("Logged out successfully");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Logout failed");
    }
  }
}
