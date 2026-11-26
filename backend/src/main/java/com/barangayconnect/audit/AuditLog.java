package com.barangayconnect.audit;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long userId;

  @Column(length = 180)
  private String actorEmail;

  @Column(nullable = false, length = 80)
  private String action;

  @Column(length = 120)
  private String entityType;

  private Long entityId;

  @Column(length = 64)
  private String ipAddress;

  @Column(nullable = false, updatable = false)
  private Instant createdAt = Instant.now();

  protected AuditLog() {}

  public AuditLog(
      Long userId,
      String actorEmail,
      String action,
      String entityType,
      Long entityId,
      String ipAddress) {
    this.userId = userId;
    this.actorEmail = actorEmail;
    this.action = action;
    this.entityType = entityType;
    this.entityId = entityId;
    this.ipAddress = ipAddress;
  }

  public Long getId() {
    return id;
  }

  public Long getUserId() {
    return userId;
  }

  public String getActorEmail() {
    return actorEmail;
  }

  public String getAction() {
    return action;
  }

  public String getEntityType() {
    return entityType;
  }

  public Long getEntityId() {
    return entityId;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }
}
