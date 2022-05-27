package com.pedalbuildpipeline.pbp.component.dto;

import com.pedalbuildpipeline.pbp.project.dto.ComponentType;

public interface Component {
  ComponentType getType();

  Integer getQuantity();

  String getDetail();
}
