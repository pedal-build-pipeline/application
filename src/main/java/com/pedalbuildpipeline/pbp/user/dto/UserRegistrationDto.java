package com.pedalbuildpipeline.pbp.user.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public record UserRegistrationDto(
    @NotEmpty @Size(min = 1, max = 128) String username,
    @NotEmpty String email,
    @NotEmpty @Size(min = 8) String password) {}
