package com.pedalbuildpipeline.pbp.user.mapping;

import com.pedalbuildpipeline.pbp.user.dto.UserDto;
import com.pedalbuildpipeline.pbp.user.dto.UserRegistrationDto;
import com.pedalbuildpipeline.pbp.user.repo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "roles", ignore = true)
  @Mapping(target = "enabled", ignore = true)
  User toUser(UserRegistrationDto userRegistrationDto);

  UserDto fromUser(User user);
}
