package com.pedalbuildpipeline.pbp.project.dto.hateoas;

import com.pedalbuildpipeline.pbp.project.controller.ProjectController;
import com.pedalbuildpipeline.pbp.project.dto.ProjectDto;
import com.pedalbuildpipeline.pbp.project.mapping.ProjectMapper;
import com.pedalbuildpipeline.pbp.project.repo.entity.Project;
import com.pedalbuildpipeline.pbp.user.controller.UserController;
import com.pedalbuildpipeline.pbp.user.dto.UserDto;
import com.pedalbuildpipeline.pbp.user.mapping.UserMapper;
import com.pedalbuildpipeline.pbp.user.repo.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@RequiredArgsConstructor
public class ProjectModelAssembler implements RepresentationModelAssembler<Project, ProjectDto> {
  private final ProjectMapper projectMapper;

  @Override
  public ProjectDto toModel(Project project) {
    return projectMapper
        .fromProject(project)
        .add(linkTo(methodOn(ProjectController.class).getProject(project.getId())).withSelfRel());
  }
}
