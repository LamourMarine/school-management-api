package com.marine.gestionecole.entity;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "refresh_tokens")
@Setter
@Getter
@NoArgsConstructor
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 500)
  private String token;

  // fetch = LAZY → Le user n'est chargé que si on l'utilise (optimisation)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "expiry_date", nullable = false)
  private Instant expiryDate;

  @Column(name = "created_at")
  private Instant createdAt;

  @Column(nullable = false)
  private Boolean revoked = false;
}
