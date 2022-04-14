package com.pedalbuildpipeline.pbp.notification.email.mailpace.config;

import static org.assertj.core.api.Assertions.assertThat;

import feign.RequestTemplate;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ServerTokenRequestInterceptorTest {

  @DisplayName("given a token, when applying the interceptor, the token is added")
  @Test
  public void doesAddToken() {
    String token = UUID.randomUUID().toString();
    ServerTokenRequestInterceptor interceptor = new ServerTokenRequestInterceptor(token);

    RequestTemplate requestTemplate = new RequestTemplate();

    interceptor.apply(requestTemplate);

    assertThat(requestTemplate.headers().get("MailPace-Server-Token").size()).isEqualTo(1);
    assertThat(requestTemplate.headers().get("MailPace-Server-Token").iterator().next())
        .isEqualTo(token);
  }
}
