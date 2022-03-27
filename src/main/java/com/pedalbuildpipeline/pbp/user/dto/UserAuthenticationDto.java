package com.pedalbuildpipeline.pbp.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@RequiredArgsConstructor
public class UserAuthenticationDto {
  @NotBlank
  private final String username;
  @NotBlank
  private final String password;
}
