package com.pedalbuildpipeline.pbp.event.listener;

import com.pedalbuildpipeline.pbp.OutboxEventListenerTestBase;
import com.pedalbuildpipeline.pbp.event.model.user.UserCreatedEvent;
import com.pedalbuildpipeline.pbp.user.repo.UserRepository;
import com.pedalbuildpipeline.pbp.user.repo.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockserver.model.JsonBody;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.JsonBody.json;

@Transactional
public class UserCreatedEventListenerIntegrationTest extends OutboxEventListenerTestBase {
  @Autowired protected UserRepository userRepository;

  @DisplayName(
      "given a user created event, when the outbox is processed, then the user receives a notification")
  @Test
  public void userCreatedNotification() throws IOException {
    User user =
        userRepository.save(
            User.builder()
                .username("test")
                .email("test@test.com")
                .password("abcd1234")
                .enabled(true)
                .build());

    mockServerClient
        .when(
            request()
                .withMethod("POST")
                .withPath("/email/v1/send")
                .withBody(
                    json(
                        new String(
                            this.getClass()
                                .getResourceAsStream(
                                    "/fixtures/emailrequests/mailpace/user-created.json")
                                .readAllBytes(),
                            StandardCharsets.UTF_8))))
        .respond(response().withStatusCode(201).withBody(json("{\"id\":1,\"status\":\"QUEUED\"}")));

    executeWithEvents(List.of(new UserCreatedEvent(user.getId(), user.getUsername())));

    mockServerClient.verify(
        request()
            .withMethod("POST")
            .withPath("/email/v1/send")
            .withBody(
                json(
                    new String(
                        this.getClass()
                            .getResourceAsStream(
                                "/fixtures/emailrequests/mailpace/user-created.json")
                            .readAllBytes(),
                        StandardCharsets.UTF_8))));
  }
}
