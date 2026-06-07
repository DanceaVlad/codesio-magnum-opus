package com.codesio.magnum_opus.court;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "courts")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@SuppressWarnings("PMD.ShortVariable")
public class Court {

  @Id
  @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
  @Column(nullable = false, updatable = false)
  private UUID id;

  @NonNull
  @Column(nullable = false, unique = true)
  private String name;

  @NonNull
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CourtType type;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @PrePersist
  void initializeCreatedAt() {
    if (createdAt == null) {
      createdAt = Instant.now();
    }
  }
}
