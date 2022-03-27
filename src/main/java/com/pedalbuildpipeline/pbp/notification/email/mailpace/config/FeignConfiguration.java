package com.pedalbuildpipeline.pbp.notification.email.mailpace.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@ConditionalOnProperty(value = "notifications.email.provider", havingValue = "mailpace")
public class FeignConfiguration {
  @Bean
  public ServerTokenRequestInterceptor serverTokenRequestInterceptor(@Value("notifications.email.credentials") String token) {
    return new ServerTokenRequestInterceptor(token);
  }
}
