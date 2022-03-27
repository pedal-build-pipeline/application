package com.pedalbuildpipeline.pbp.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthenticationResponse {
  private final String idToken;
}
