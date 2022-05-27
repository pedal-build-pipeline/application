package com.pedalbuildpipeline.pbp.project.repo.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Blob;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "project_media")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProjectMedia {
  @Id
  @Column(name = "id", nullable = false, updatable = false)
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "project", referencedColumnName = "id", nullable = false, updatable = false)
  private Project project;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "content_type", nullable = false)
  private String contentType;

  @Column(name = "size_in_bytes", nullable = false)
  private Long size;

  @OneToOne(cascade = CascadeType.ALL)
  private ProjectMediaBOMMetadata bomMetadata;

  @OneToMany(cascade = CascadeType.MERGE)
  @ToString.Exclude
  private Set<BaseProjectComponent> componentsFromBOM;

  @Lob
  @Column(name = "data", nullable = false)
  @ToString.Exclude
  private Blob data;

  @Lob
  @Column(name = "preview", nullable = false)
  @ToString.Exclude
  private Blob preview;
}
