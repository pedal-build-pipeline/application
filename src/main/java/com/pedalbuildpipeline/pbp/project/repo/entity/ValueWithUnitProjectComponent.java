package com.pedalbuildpipeline.pbp.project.repo.entity;

import com.pedalbuildpipeline.pbp.component.dto.ValueWithUnitComponent;
import com.pedalbuildpipeline.pbp.project.dto.UnitMultiplier;
import com.pedalbuildpipeline.pbp.project.dto.UnitType;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@DiscriminatorValue("VALUE_WITH_UNIT")
public class ValueWithUnitProjectComponent extends BaseProjectComponent implements ValueWithUnitComponent {
  @Column(name = "value", nullable = false)
  private Float value;

  @Enumerated(EnumType.STRING)
  @Column(name = "unit_type", nullable = false)
  @Type(type = "psql_enum")
  private UnitType unitType;

  @Enumerated(EnumType.STRING)
  @Column(name = "unit_multiplier", nullable = false)
  @Type(type = "psql_enum")
  private UnitMultiplier unitMultiplier;
}
