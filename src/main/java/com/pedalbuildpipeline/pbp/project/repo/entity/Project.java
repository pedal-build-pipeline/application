package com.pedalbuildpipeline.pbp.project.repo.entity;

import com.pedalbuildpipeline.pbp.user.repo.entity.User;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "project")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EntityListeners(AuditingEntityListener.class)
public class Project {
  @Id
  @Column(name = "id", nullable = false, updatable = false)
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "creator", referencedColumnName = "id", nullable = false, updatable = false)
  @CreatedBy
  private User creator;

  @Column(name = "created_at", nullable = false, updatable = false)
  @CreationTimestamp
  private OffsetDateTime createdAt;

  @Column(name = "last_updated_at", nullable = false, updatable = false)
  @UpdateTimestamp
  private OffsetDateTime lastUpdatedAt;

  @Column(name = "name", nullable = false)
  private String name;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @ToString.Exclude
  private Set<ProjectMedia> media;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @ToString.Exclude
  private Set<BaseProjectComponent> components;
}
