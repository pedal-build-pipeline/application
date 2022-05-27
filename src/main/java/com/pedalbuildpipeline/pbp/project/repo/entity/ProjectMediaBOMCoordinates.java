package com.pedalbuildpipeline.pbp.project.repo.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "project_media_bom_coordinates")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProjectMediaBOMCoordinates {
  @Id
  @Column(name = "id", nullable = false, updatable = false)
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "bom_metadata_id", referencedColumnName = "id", nullable = false, updatable = false)
  private ProjectMediaBOMMetadata bomMetadata;

  @Column(name = "page", nullable = false, updatable = false)
  private Integer page;
}
