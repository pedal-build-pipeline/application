package com.pedalbuildpipeline.pbp.user.service;

import com.pedalbuildpipeline.pbp.event.model.user.UserCreatedEvent;
import com.pedalbuildpipeline.pbp.event.outbox.service.OutboxService;
import com.pedalbuildpipeline.pbp.features.toggles.UserFeatureToggles;
import com.pedalbuildpipeline.pbp.user.exception.UserRegistrationDisabledException;
import com.pedalbuildpipeline.pbp.user.exception.UsernameAlreadyInUseException;
import com.pedalbuildpipeline.pbp.user.repo.UserRepository;
import com.pedalbuildpipeline.pbp.user.repo.entity.User;
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final OutboxService outboxService;
  private final UserFeatureToggles userFeatureToggles;

  public User registerUser(User user) {
    if (userFeatureToggles.isRegistrationEnabled()) {
      log.info(
          "Attempting to register user with username {} and email {}",
          user.getUsername(),
          user.getEmail());

      Optional<User> userWithUsername = userRepository.findByUsernameEquals(user.getUsername());
      if (userWithUsername.isEmpty()) {
        User userToSave =
            User.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword()))
                .enabled(true)
                .build();

        User createdUser = userRepository.save(userToSave);
        outboxService.createEntry(new UserCreatedEvent(createdUser.getId()));

        return createdUser;
      } else {
        throw new UsernameAlreadyInUseException(user.getUsername());
      }
    } else {
      throw new UserRegistrationDisabledException();
    }
  }

  public Optional<User> findByUsername(String username) {
    return userRepository.findByUsernameEquals(username);
  }

  public Optional<User> findUser(UUID id) {
    return userRepository.findById(id);
  }

  public Page<User> searchUsers(Optional<String> username, Pageable pageable) {
    return username
        .map(desiredUsername -> userRepository.findByUsernameContaining(desiredUsername, pageable))
        .orElseGet(() -> userRepository.findAll(pageable));
  }
}
