package com.pedalbuildpipeline.pbp.project.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public record ProjectCreateDto(
        @NotEmpty @Size(min = 1, max = 256) String name,
        @NotNull List<ProjectMediaCreateDto> media
        ) {}
