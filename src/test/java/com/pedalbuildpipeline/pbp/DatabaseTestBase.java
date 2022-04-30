package com.pedalbuildpipeline.pbp;

import org.junit.jupiter.api.parallel.ResourceLock;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;

@ContextConfiguration(initializers = {DatabaseTestBase.PostgresInitializer.class})
@ResourceLock(value = "psqlContainer")
public class DatabaseTestBase {
  public static PostgreSQLContainer<?> postgreSQLContainer;

  static {
    postgreSQLContainer = new PostgreSQLContainer<>("postgres:14.2");
    postgreSQLContainer.start();

    Runtime.getRuntime().addShutdownHook(new Thread(() -> postgreSQLContainer.stop()));
  }

  public static final class PostgresInitializer
      implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
      TestPropertyValues.of(
              "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
              "spring.datasource.username=" + postgreSQLContainer.getUsername(),
              "spring.datasource.password=" + postgreSQLContainer.getPassword())
          .applyTo(applicationContext.getEnvironment());
    }
  }
}
