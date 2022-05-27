package com.pedalbuildpipeline.pbp.project.dto;

import org.springframework.http.MediaType;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.InputStream;

public record ProjectMediaCreateDto(
        @NotEmpty @Size(min = 1, max = 256) String name,
        @NotNull MediaType contentType,
        @NotNull @Size(min = 1) Long size,
        @NotNull InputStream data) {}
