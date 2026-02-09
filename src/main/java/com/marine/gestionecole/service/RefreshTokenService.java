package com.marine.gestionecole.service;

import com.marine.gestionecole.entity.RefreshToken;
import com.marine.gestionecole.entity.User;
import com.marine.gestionecole.repository.RefreshTokenRepository;
import com.marine.gestionecole.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

// @Service = "C'est un service Spring, il contient de la logique métier"
@Service
public class RefreshTokenService {

    // @Value = lit une valeur depuis application.properties
    // Si la propriété n'existe pas, utilise la valeur par défaut (7 jours)
    @Value("${jwt.refresh.expiration:604800000}")
    private Long refreshTokenDurationMs; // 7 jours en millisecondes

    // Spring injecte automatiquement ces dépendances (Dependency Injection)
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    // Constructeur : Spring injecte les repositories automatiquement
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    /**
     * Créer un nouveau refresh token pour un utilisateur
     * @param userId ID de l'utilisateur
     * @return Le refresh token créé
     */
    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        // 1. Récupérer l'utilisateur depuis la BDD
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // 2. Créer un nouveau refresh token
        RefreshToken refreshToken = new RefreshToken();
        
        // 3. Générer un token unique avec UUID (ex: "a3f5b2c1-...")
        refreshToken.setToken(UUID.randomUUID().toString());
        
        // 4. Associer le token à l'utilisateur
        refreshToken.setUser(user);
        
        // 5. Calculer la date d'expiration (maintenant + 7 jours)
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        
        // 6. Date de création
        refreshToken.setCreatedAt(Instant.now());
        
        // 7. Par défaut, le token n'est pas révoqué
        refreshToken.setRevoked(false);

        // 8. Sauvegarder en BDD et retourner
        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Vérifier si un refresh token est expiré
     * @param token Le refresh token à vérifier
     * @return Le token si valide
     * @throws RuntimeException si le token est expiré
     */
    public RefreshToken verifyExpiration(RefreshToken token) {
        // Comparer la date d'expiration avec maintenant
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            // Le token est expiré, on le supprime
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please login again.");
        }
        
        // Le token est encore valide
        return token;
    }

    /**
     * Chercher un refresh token par sa valeur
     * @param token La valeur du token
     * @return Optional contenant le token si trouvé
     */
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Révoquer tous les refresh tokens d'un utilisateur (lors du logout)
     * @param user L'utilisateur dont on révoque les tokens
     */
    @Transactional
    public void revokeByUser(User user) {
        refreshTokenRepository.revokeAllByUser(user);
    }
}
