package com.pedalbuildpipeline.pbp.project.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import org.springframework.hateoas.RepresentationModel;

import java.util.UUID;

@Builder
@Jacksonized
@Getter
public class ProjectDto extends RepresentationModel<ProjectDto> {
  private UUID id;

}
