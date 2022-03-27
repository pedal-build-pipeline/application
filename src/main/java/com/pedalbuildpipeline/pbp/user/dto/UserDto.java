package com.pedalbuildpipeline.pbp.user.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import org.springframework.hateoas.RepresentationModel;

@Builder
@Jacksonized
@Getter
public class UserDto extends RepresentationModel<UserDto> {
  private UUID id;
  private String username;
  private String email;
}
