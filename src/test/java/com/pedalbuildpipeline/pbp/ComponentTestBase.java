package com.pedalbuildpipeline.pbp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.client.MockServerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.utility.DockerImageName;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ContextConfiguration(
    initializers = {
      DatabaseTestBase.PostgresInitializer.class,
      ComponentTestBase.MockServerInitializer.class
    })
public class ComponentTestBase extends DatabaseTestBase {
  @Autowired protected ObjectMapper objectMapper;

  @Autowired protected MockMvc mockMvc;

  protected MockServerClient mockServerClient;

  @BeforeEach
  public void beforeEach() {
    mockServerClient = new MockServerClient(mockServer.getHost(), mockServer.getServerPort());
    mockServerClient.reset();

    Runtime.getRuntime().addShutdownHook(new Thread(() -> mockServerClient.stop()));
  }

  public static MockServerContainer mockServer;

  static {
    mockServer = new MockServerContainer(DockerImageName.parse("mockserver/mockserver:5.13.2"));
    mockServer.start();
  }

  protected static final String BASE64_JWT_SECRET =
      Base64.toBase64String(Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded());

  public static final class MockServerInitializer
      implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
      TestPropertyValues.of("notifications.email.api-url=" + mockServer.getEndpoint() + "/email")
          .applyTo(applicationContext.getEnvironment());
    }
  }

  @DynamicPropertySource
  static void jwtProperties(DynamicPropertyRegistry registry) {
    registry.add("security.jwt.base64Secret", () -> BASE64_JWT_SECRET);
  }

  protected String asJson(final Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
