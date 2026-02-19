package com.marine.gestionecole.service;

import com.marine.gestionecole.dto.AuthResponse;
import com.marine.gestionecole.dto.LoginRequest;
import com.marine.gestionecole.dto.RegisterRequest;
import com.marine.gestionecole.entity.RefreshToken;
import com.marine.gestionecole.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

  @Autowired
  private UserService userService;

  @Autowired
  private JwtService jwtService;

  @Autowired
  private RefreshTokenService refreshTokenService;

  @Autowired
  private AuthenticationManager authenticationManager;

  public AuthResponse register(RegisterRequest request) {
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

    return new AuthResponse(
      accessToken,
      refreshTokenString,
      user.getUsername(),
      user.getRole().name()
    );
  }

  public AuthResponse login(LoginRequest request) {
    authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(
        request.getUsername(),
        request.getPassword()
      )
    );

    //Récuperer le user
    User user = userService
      .findByUsername(request.getUsername())
      .orElseThrow(() -> new RuntimeException("User not found"));

    //charger les userDetails
    UserDetails userDetails = userService.loadUserByUsername(
      request.getUsername()
    );

    // Créer le refresh token (retourne l'entité RefreshToken)
    RefreshToken refreshToken = refreshTokenService.createRefreshToken(
      user.getId()
    );
    // Extraire le token sous forme de String
    String refreshTokenString = refreshToken.getToken();

    String accessToken = jwtService.generateToken(userDetails);

    return new AuthResponse(
      accessToken,
      refreshTokenString,
      user.getUsername(),
      user.getRole().name()
    );
  }

  public AuthResponse refresh(String refreshToken) {
    // Chercher le refresh token en base
    RefreshToken token = refreshTokenService.findByToken(refreshToken);

    // Vérifier qu'il n'est pas expiré
    RefreshToken validatedToken = refreshTokenService.verifyExpiration(token);

    // Récupérer le user associé
    User user = validatedToken.getUser();

    // Générer  nouveau access token
    UserDetails userDetails = userService.loadUserByUsername(
      user.getUsername()
    );
    String newAccessToken = jwtService.generateToken(userDetails);

    // Révoquer l'ancien
    refreshTokenService.revokeToken(token.getToken());

    // Créer un nouveau refresh token
    RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(
      user.getId()
    );

    return new AuthResponse(
      newAccessToken,
      newRefreshToken.getToken(),
      user.getUsername(),
      user.getRole().name()
    );
  }

    public void logout(String refreshToken) {
    // Chercher le refresh token en base
    RefreshToken token = refreshTokenService.findByToken(refreshToken);

    // Révoquer tous les tokens de cet utilisateur
    refreshTokenService.revokeByUser(token.getUser());

    }
}
