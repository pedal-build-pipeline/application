package com.pedalbuildpipeline.pbp.notification.email.mailpace.model;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Builder
@Getter
@Jacksonized
public class SendEmailResponse {
  private int id;
  private String status;
}
