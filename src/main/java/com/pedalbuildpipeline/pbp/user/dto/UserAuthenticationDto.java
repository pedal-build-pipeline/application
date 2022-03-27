package com.pedalbuildpipeline.pbp.user.dto;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserAuthenticationDto {
  @NotBlank private final String username;
  @NotBlank private final String password;
}
