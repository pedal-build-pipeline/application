package com.pedalbuildpipeline.pbp.user.repo;

import com.pedalbuildpipeline.pbp.DatabaseTestBase;
import com.pedalbuildpipeline.pbp.user.repo.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest extends DatabaseTestBase {
  @Autowired UserRepository userRepository;

  @Test
  @DisplayName("given a valid user, when saving the user, the user does save")
  void createUser() {
    User user =
        User.builder()
            .password("{noop}test123")
            .roles(new String[] {"ROLE_A", "ROLE_B", "ROLE_C"})
            .email("test@test.com")
            .enabled(true)
            .build();

    User savedUser = userRepository.save(user);

    assertThat(savedUser.getId()).isNotNull();
  }

  @Test
  @DisplayName("given a user id, when retrieving the user by id, the user is returned")
  @Sql("/fixtures/user/test_users.sql")
  void getUserById() {
    Optional<User> user =
        userRepository.findById(UUID.fromString("988f02fd-c931-45df-9187-af33ee64f4ec"));

    assertThat(user.isPresent()).isEqualTo(true);
    assertThat(user.get().getId()).isEqualTo(UUID.fromString("988f02fd-c931-45df-9187-af33ee64f4ec"));
    assertThat(user.get().getUsername()).isEqualTo("test1");
    assertThat(user.get().getPassword()).isEqualTo("{noop}test1");
    assertThat(user.get().getRoles()).isEqualTo(new String[] {"ROLE_A", "ROLE_B"});
    assertThat(user.get().isEnabled()).isEqualTo(true);
  }

  @Test
  @DisplayName("given a username, when retrieving the user by username, the user is returned")
  @Sql("/fixtures/user/test_users.sql")
  void getByUsername() {
    Optional<User> user = userRepository.findByUsernameEquals("test3");

    assertThat(user.isPresent()).isEqualTo(true);
    assertThat(user.get().getId()).isEqualTo(UUID.fromString("0ac07687-a759-4311-bb19-5e39c21483a3"));
    assertThat(user.get().getUsername()).isEqualTo("test3");
    assertThat(user.get().getPassword()).isEqualTo("{noop}test3");
    assertThat(user.get().getRoles()).isEqualTo(new String[] {"ROLE_C"});
    assertThat(user.get().isEnabled()).isEqualTo(true);
  }
}
