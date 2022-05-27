package com.pedalbuildpipeline.pbp.project.controller;

import com.pedalbuildpipeline.pbp.ResourceType;
import com.pedalbuildpipeline.pbp.project.dto.ProjectCreateDto;
import com.pedalbuildpipeline.pbp.project.dto.ProjectDto;
import com.pedalbuildpipeline.pbp.project.dto.hateoas.ProjectModelAssembler;
import com.pedalbuildpipeline.pbp.project.mapping.ProjectMapper;
import com.pedalbuildpipeline.pbp.project.repo.entity.Project;
import com.pedalbuildpipeline.pbp.project.service.ProjectService;
import com.pedalbuildpipeline.pbp.project.validation.ValidProjectMedia;
import com.pedalbuildpipeline.pbp.user.controller.UserController;
import com.pedalbuildpipeline.pbp.user.dto.UserRegistrationDto;
import com.pedalbuildpipeline.pbp.user.repo.entity.User;
import com.pedalbuildpipeline.pbp.validation.AcceptableContentType;
import com.pedalbuildpipeline.pbp.web.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/projects")
@Validated
@RequiredArgsConstructor
public class ProjectController {
  private final ProjectService projectService;
  private final ProjectMapper projectMapper;
  private final ProjectModelAssembler projectModelAssembler;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(description = "Create a new project")
  @ApiResponses({
    @ApiResponse(description = "Project is successfully created", responseCode = "201"),
    @ApiResponse(description = "The request data is invalid", responseCode = "400")
  })
  public ResponseEntity<Void> createProject(
      @Valid @RequestPart("json") ProjectCreateDto projectCreateDto,
      @RequestPart("media") @ValidProjectMedia MultipartFile[] media) {
    Project createdProject =
        projectService.createProject(projectMapper.toProject(projectCreateDto), media);

    return ResponseEntity.created(
            linkTo(methodOn(ProjectController.class).getProject(createdProject.getId())).toUri())
        .build();
  }

  @GetMapping("/{id}")
  @Operation(description = "Retrieve a project by its identifier")
  @ApiResponses({
    @ApiResponse(description = "Project was found and returned", responseCode = "200"),
    @ApiResponse(description = "Invalid project id", responseCode = "400"),
    @ApiResponse(description = "Project with the given id was not found", responseCode = "404")
  })
  public ResponseEntity<ProjectDto> getProject(@NotNull @PathVariable("id") UUID id) {
    return ResponseEntity.ok(
        projectService
            .findProject(id)
            .map(projectModelAssembler::toModel)
            .orElseThrow(() -> new ResourceNotFoundException(ResourceType.PROJECT, id.toString())));
  }
}
