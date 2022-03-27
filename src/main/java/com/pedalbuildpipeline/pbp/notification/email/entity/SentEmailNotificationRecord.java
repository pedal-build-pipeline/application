package com.pedalbuildpipeline.pbp.notification.email.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.util.Map;
import java.util.UUID;
import javax.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Entity
@Table(name = "sent_email_notification_record")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SentEmailNotificationRecord {
  @Id
  @Column(name = "id", nullable = false, updatable = false)
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
  private UUID id;

  @Column(name = "user_id", updatable = false, nullable = false)
  private UUID userId;

  @Column(name = "provider_id", updatable = false, nullable = false)
  private String providerId;

  @Column(name = "provider", updatable = false, nullable = false)
  private String provider;

  @Column(name = "status", nullable = false)
  private String status;

  @Type(type = "jsonb")
  @Column(name = "metadata", columnDefinition = "jsonb")
  private Map<String, Object> metadata;
}
