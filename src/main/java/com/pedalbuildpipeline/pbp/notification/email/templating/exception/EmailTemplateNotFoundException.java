package com.pedalbuildpipeline.pbp.notification.email.templating.exception;

import com.pedalbuildpipeline.pbp.notification.NotificationType;
import lombok.Getter;

@Getter
public class EmailTemplateNotFoundException extends Exception {
  private final NotificationType notificationType;

  public EmailTemplateNotFoundException(String message, NotificationType notificationType) {
    super(message);
    this.notificationType = notificationType;
  }
}
