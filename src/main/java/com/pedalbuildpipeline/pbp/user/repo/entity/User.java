package com.pedalbuildpipeline.pbp.user.repo.entity;

import com.pedalbuildpipeline.pbp.project.repo.entity.Project;
import com.vladmihalcea.hibernate.type.array.StringArrayType;

import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

@Entity
@Table(name = "user_account")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TypeDefs({@TypeDef(name = "string-array", typeClass = StringArrayType.class)})
@ToString
public class User {
  @Id
  @Column(name = "id", nullable = false, updatable = false)
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
  private UUID id;

  @Column(name = "username", unique = true, nullable = false, updatable = false)
  private String username;

  @Column(name = "email", unique = true, nullable = false)
  private String email;

  @Column(name = "password", nullable = false)
  @ToString.Exclude
  private String password;

  @Column(name = "roles", columnDefinition = "text[]")
  @Type(type = "string-array")
  private String[] roles;

  @Column(name = "enabled")
  private boolean enabled;

  @OneToMany
  @ToString.Exclude
  private Set<Project> projects;
}
