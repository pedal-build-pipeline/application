package com.pedalbuildpipeline.pbp.user.dto.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.pedalbuildpipeline.pbp.user.controller.UserController;
import com.pedalbuildpipeline.pbp.user.dto.UserDto;
import com.pedalbuildpipeline.pbp.user.mapping.UserMapper;
import com.pedalbuildpipeline.pbp.user.repo.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserModelAssembler implements RepresentationModelAssembler<User, UserDto> {
  private final UserMapper userMapper;

  @Override
  public UserDto toModel(User user) {
    return userMapper
        .fromUser(user)
        .add(linkTo(methodOn(UserController.class).getUser(user.getId())).withSelfRel());
  }
}
