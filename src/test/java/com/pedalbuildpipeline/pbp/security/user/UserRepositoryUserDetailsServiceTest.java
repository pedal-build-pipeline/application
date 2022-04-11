package com.pedalbuildpipeline.pbp.security.user;

import com.pedalbuildpipeline.pbp.user.repo.entity.User;
import com.pedalbuildpipeline.pbp.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryUserDetailsServiceTest {
  @InjectMocks private UserRepositoryUserDetailsService service;

  @Mock private UserService userService;

  @DisplayName(
      "given no user with the username is found, when loading user details, then an exception is thrown")
  @Test
  public void noUserFound() {
    when(userService.findByUsername("test")).thenReturn(Optional.empty());

    assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("test"));

    verify(userService).findByUsername("test");
  }

  @DisplayName(
      "given a user with the username is found for a fully populated user, when loading user details, then the user's details are returned")
  @Test
  public void userFound() {
    when(userService.findByUsername("test"))
        .thenReturn(
            Optional.of(
                new User(
                    UUID.randomUUID(),
                    "test",
                    "test@test.com",
                    "abad34983Afd",
                    new String[] {"AUTH1", "AUTH2", "AUTH2"},
                    true)));

    UserDetails userDetails = service.loadUserByUsername("test");

    assertThat(userDetails.getUsername()).isEqualTo("test");
    assertThat(userDetails.getPassword()).isEqualTo("abad34983Afd");
    assertThat(
            userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()))
        .isEqualTo(List.of("ROLE_AUTH1", "ROLE_AUTH2"));
    assertThat(userDetails.isEnabled()).isEqualTo(true);
  }

  @DisplayName(
      "given a user with the username is found for a partially populated user, when loading user details, then the user's details are returned")
  @Test
  public void userFoundPartial() {
    when(userService.findByUsername("test"))
        .thenReturn(
            Optional.of(
                new User(UUID.randomUUID(), "test", "test@test.com", "abad34983Afd", null, false)));

    UserDetails userDetails = service.loadUserByUsername("test");

    assertThat(userDetails.getUsername()).isEqualTo("test");
    assertThat(userDetails.getPassword()).isEqualTo("abad34983Afd");
    assertThat(userDetails.getAuthorities()).isEmpty();
    assertThat(userDetails.isEnabled()).isEqualTo(false);
  }
}
