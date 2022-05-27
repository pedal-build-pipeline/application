package com.pedalbuildpipeline.pbp.project.repo.entity;

import com.pedalbuildpipeline.pbp.component.dto.Component;
import com.pedalbuildpipeline.pbp.project.dto.ComponentType;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import lombok.*;
import org.hibernate.annotations.DiscriminatorFormula;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "project_component")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorFormula(
    "case when type in ('RESISTOR', 'CAPACITOR', 'POTENTIOMETER', 'TRIMMER') then 'VALUE_WITH_UNIT' else 'GENERIC'")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@TypeDef(name = "psql_enum", typeClass = PostgreSQLEnumType.class)
public class BaseProjectComponent implements Component {
  @Id
  @Column(name = "id", nullable = false, updatable = false)
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "project", referencedColumnName = "id", nullable = false, updatable = false)
  private Project project;

  @ManyToOne
  @JoinColumn(name = "source_media", referencedColumnName = "id")
  private ProjectMedia sourceMedia;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  @Type(type = "psql_enum")
  private ComponentType type;

  @Column(name = "quantity", nullable = false)
  private Integer quantity;

  @Column(name = "detail")
  private String detail;
}
