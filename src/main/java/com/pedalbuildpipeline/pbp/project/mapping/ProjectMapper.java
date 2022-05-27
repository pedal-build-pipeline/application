package com.pedalbuildpipeline.pbp.project.mapping;

import com.pedalbuildpipeline.pbp.project.dto.ProjectCreateDto;
import com.pedalbuildpipeline.pbp.project.dto.ProjectDto;
import com.pedalbuildpipeline.pbp.project.repo.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "creator", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "lastUpdatedAt", ignore = true)
  @Mapping(target = "components", ignore = true)
  @Mapping(target = "media", ignore = true)
  Project toProject(ProjectCreateDto projectCreateDto);

  ProjectDto fromProject(Project project);
}
