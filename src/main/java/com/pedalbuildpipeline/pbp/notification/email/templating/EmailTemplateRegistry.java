package com.pedalbuildpipeline.pbp.notification.email.templating;

import com.pedalbuildpipeline.pbp.notification.NotificationType;
import com.pedalbuildpipeline.pbp.notification.email.templating.exception.EmailTemplateNotFoundException;
import com.pedalbuildpipeline.pbp.notification.email.templating.model.EmailTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EmailTemplateRegistry {
  private final Map<NotificationType, EmailTemplate> templateByNotificationType;

  public EmailTemplate getEmailTemplate(NotificationType notificationType)
      throws EmailTemplateNotFoundException {
    return Optional.ofNullable(templateByNotificationType.get(notificationType))
        .orElseThrow(
            () ->
                new EmailTemplateNotFoundException(
                    "Unable to find email template for notification of type ("
                        + notificationType.name()
                        + ")",
                    notificationType));
  }
}
