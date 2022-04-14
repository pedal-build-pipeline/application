package com.pedalbuildpipeline.pbp.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.pedalbuildpipeline.pbp.event.model.user.UserCreatedEvent;
import com.pedalbuildpipeline.pbp.event.outbox.service.OutboxService;
import com.pedalbuildpipeline.pbp.features.toggles.UserFeatureToggles;
import com.pedalbuildpipeline.pbp.user.exception.UserRegistrationDisabledException;
import com.pedalbuildpipeline.pbp.user.exception.UsernameAlreadyInUseException;
import com.pedalbuildpipeline.pbp.user.repo.UserRepository;
import com.pedalbuildpipeline.pbp.user.repo.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  @InjectMocks private UserService userService;

  @Mock private PasswordEncoder passwordEncoder;
  @Mock private UserRepository userRepository;
  @Mock private OutboxService outboxService;
  @Mock private UserFeatureToggles userFeatureToggles;

  @DisplayName(
      "given user registration is disabled, when registering a user, an exception is thrown")
  @Test
  public void registerWhileRegistrationDisabled() {
    when(userFeatureToggles.isRegistrationEnabled()).thenReturn(false);

    assertThrows(
        UserRegistrationDisabledException.class,
        () -> userService.registerUser(User.builder().build()));

    verify(userFeatureToggles).isRegistrationEnabled();
  }

  @DisplayName(
      "given a user tries registering with a username in use, when the user is registered, then an exception is thrown")
  @Test
  public void registerWithInUseUsername() {
    when(userFeatureToggles.isRegistrationEnabled()).thenReturn(true);
    when(userRepository.findByUsernameEquals("test"))
        .thenReturn(Optional.of(User.builder().username("test").build()));

    assertThrows(
        UsernameAlreadyInUseException.class,
        () -> userService.registerUser(User.builder().username("test").build()));

    verify(userFeatureToggles).isRegistrationEnabled();
    verify(userRepository).findByUsernameEquals("test");
  }

  @DisplayName(
      "given user registration is enabled, when a user is registered, then the proper user is created and corresponding outbox entry created")
  @Test
  public void registerUser() {
    User savedUser = mock(User.class);
    UUID userId = UUID.randomUUID();

    when(userFeatureToggles.isRegistrationEnabled()).thenReturn(true);
    when(userRepository.findByUsernameEquals("test")).thenReturn(Optional.empty());

    when(passwordEncoder.encode("abcd1234")).thenReturn("4321dcba");
    when(userRepository.save(
            argThat(
                (user) ->
                    user.isEnabled()
                        && "test".equals(user.getUsername())
                        && "test@test.com".equals(user.getEmail())
                        && "4321dcba".equals(user.getPassword()))))
        .thenReturn(savedUser);
    when(savedUser.getId()).thenReturn(userId);

    User user =
        userService.registerUser(
            User.builder()
                .username("test")
                .email("test@test.com")
                .password("abcd1234")
                .enabled(false)
                .build());

    assertThat(user).isEqualTo(savedUser);

    verify(userFeatureToggles).isRegistrationEnabled();
    verify(userRepository).findByUsernameEquals("test");
    verify(passwordEncoder).encode("abcd1234");
    verify(userRepository)
        .save(
            argThat(
                (savingUser) ->
                    savingUser.isEnabled()
                        && "test".equals(savingUser.getUsername())
                        && "test@test.com".equals(savingUser.getEmail())
                        && "4321dcba".equals(savingUser.getPassword())));
    verify(outboxService)
        .createEntry(
            argThat(
                (event) ->
                    event instanceof UserCreatedEvent
                        && userId.equals(((UserCreatedEvent) event).id())));
  }

  @DisplayName("given a username, when finding by username, then the user is returned")
  @Test
  public void findByUsername() {
    User user = mock(User.class);

    when(userRepository.findByUsernameEquals("test")).thenReturn(Optional.of(user));

    Optional<User> maybeUser = userService.findByUsername("test");

    assertThat(maybeUser).isEqualTo(Optional.of(user));

    verify(userRepository).findByUsernameEquals("test");
  }

  @DisplayName("given a user id, when finding by user id, then the user is returned")
  @Test
  public void findById() {
    UUID userId = UUID.randomUUID();
    User user = mock(User.class);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    Optional<User> maybeUser = userService.findUser(userId);

    assertThat(maybeUser).isEqualTo(Optional.of(user));

    verify(userRepository).findById(userId);
  }

  @DisplayName(
      "given a user search request with username, when searching for users, then the users are searched by username containing and returned")
  @Test
  public void searchByUsername() {
    Page<User> page = mock(Page.class);

    when(userRepository.findByUsernameContaining("test", PageRequest.of(2, 10))).thenReturn(page);

    Page<User> users = userService.searchUsers(Optional.of("test"), PageRequest.of(2, 10));

    assertThat(users).isEqualTo(page);

    verify(userRepository).findByUsernameContaining("test", PageRequest.of(2, 10));
  }

  @DisplayName(
      "given a user search request without search criteria, when searching for users, then all users are found")
  @Test
  public void searchNoCriteria() {
    Page<User> page = mock(Page.class);

    when(userRepository.findAll(PageRequest.of(2, 10))).thenReturn(page);

    Page<User> users = userService.searchUsers(Optional.empty(), PageRequest.of(2, 10));

    assertThat(users).isEqualTo(page);

    verify(userRepository).findAll(PageRequest.of(2, 10));
  }
}
