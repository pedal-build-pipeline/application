package com.pedalbuildpipeline.pbp.component.dto;

import com.pedalbuildpipeline.pbp.project.dto.UnitType;

public interface ValueWithUnitComponent extends Component {
  Float getValue();

  UnitType getUnitType();
}
