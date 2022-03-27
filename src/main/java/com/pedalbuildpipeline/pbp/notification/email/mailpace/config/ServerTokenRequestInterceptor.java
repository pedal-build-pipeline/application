package com.pedalbuildpipeline.pbp.notification.email.mailpace.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ServerTokenRequestInterceptor implements RequestInterceptor {
  private final String token;

  @Override
  public void apply(RequestTemplate template) {
    template.header("MailPace-Server-Token", token);
  }
}
