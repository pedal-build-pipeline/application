package com.pedalbuildpipeline.pbp.project.repo.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "project_media_bom_metadata")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProjectMediaBOMMetadata {
  @Id
  @Column(name = "id", nullable = false, updatable = false)
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
  private UUID id;

  @OneToOne(mappedBy = "bomMetadata")
  private ProjectMedia media;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private Set<ProjectMediaBOMCoordinates> bomCoordinates;
}
