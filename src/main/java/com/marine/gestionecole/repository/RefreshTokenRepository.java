package com.marine.gestionecole.repository;

import com.marine.gestionecole.entity.RefreshToken;
import com.marine.gestionecole.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

// JpaRepository<RefreshToken, Long> signifie :
// - On manipule des objets RefreshToken
// - La clé primaire est de type Long
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // Spring Data génère automatiquement cette requête :
    // SELECT * FROM refresh_tokens WHERE token = ?
    Optional<RefreshToken> findByToken(String token);

    // Supprimer tous les refresh tokens d'un user
    // @Transactional = tout se fait dans une transaction (tout ou rien)
    // @Modifying = cette requête modifie des données (DELETE/UPDATE)
    @Transactional
    @Modifying
    void deleteByUser(User user);

    // Requête personnalisée pour révoquer tous les tokens d'un user
    // @Query = on écrit nous-mêmes la requête (en JPQL, pas SQL)
    // JPQL = langage de requête orienté objet (on parle d'objets, pas de tables)
    @Transactional
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.user = :user")
    void revokeAllByUser(@Param("user") User user);

    // Révoquer un token spécifique par sa valeur
    @Transactional
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.token = :token")
    void revokeByToken(@Param("token") String token);

}
