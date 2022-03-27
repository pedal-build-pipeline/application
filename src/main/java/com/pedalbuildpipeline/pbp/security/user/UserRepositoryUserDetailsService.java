package com.pedalbuildpipeline.pbp.security.user;

import com.pedalbuildpipeline.pbp.user.service.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRepositoryUserDetailsService implements UserDetailsService {
  private final UserService userService;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userService
        .findByUsername(username)
        .map(
            (user) ->
                User.withUsername(user.getUsername())
                    .password(user.getPassword())
                    .roles(
                        Arrays.stream(Optional.ofNullable(user.getRoles()).orElse(new String[0]))
                            .distinct()
                            .toArray(String[]::new))
                    .disabled(!user.isEnabled())
                    .build())
        .orElseThrow(
            () -> {
              throw new UsernameNotFoundException("Unable to find user with username " + username);
            });
  }
}
