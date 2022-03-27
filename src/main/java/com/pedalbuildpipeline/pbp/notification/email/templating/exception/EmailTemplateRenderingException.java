package com.pedalbuildpipeline.pbp.notification.email.templating.exception;

import com.pedalbuildpipeline.pbp.notification.NotificationType;
import lombok.Getter;

@Getter
public class EmailTemplateRenderingException extends Exception {
  private NotificationType notificationType;

  public EmailTemplateRenderingException(String message, Throwable cause, NotificationType notificationType) {
    super(message, cause);
    this.notificationType = notificationType;
  }
}
