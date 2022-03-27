package com.pedalbuildpipeline.pbp.notification.email.templating;

import com.pedalbuildpipeline.pbp.notification.NotificationType;
import com.pedalbuildpipeline.pbp.notification.email.templating.exception.EmailTemplateNotFoundException;
import com.pedalbuildpipeline.pbp.notification.email.templating.exception.EmailTemplateRenderingException;
import com.pedalbuildpipeline.pbp.notification.email.templating.model.EmailTemplate;
import com.pedalbuildpipeline.pbp.notification.model.email.EmailBody;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailTemplatingService {
  private final EmailTemplateRegistry emailTemplateRegistry;

  public EmailBody renderEmailBodyForNotificationType(
      NotificationType notificationType, Map<String, Object> parameters)
      throws EmailTemplateNotFoundException, EmailTemplateRenderingException {
    EmailTemplate emailTemplate = emailTemplateRegistry.getEmailTemplate(notificationType);

    try {
      return new EmailBody(
          emailTemplate.htmlTemplate().apply(parameters),
          emailTemplate.textTemplate().apply(parameters));
    } catch (IOException e) {
      throw new EmailTemplateRenderingException(
          "Unable to render template for notification type (" + notificationType.name() + ")",
          e,
          notificationType);
    }
  }
}
