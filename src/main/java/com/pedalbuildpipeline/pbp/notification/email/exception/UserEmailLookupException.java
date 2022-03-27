package com.pedalbuildpipeline.pbp.notification.email.exception;

import java.util.UUID;

public class UserEmailLookupException extends Exception {
  private final UUID userId;

  public UserEmailLookupException(String message, UUID userId) {
    super(message);
    this.userId = userId;
  }
}
