package com.pedalbuildpipeline.pbp.notification.email.templating;

import com.github.jknack.handlebars.Template;
import com.pedalbuildpipeline.pbp.notification.NotificationType;
import com.pedalbuildpipeline.pbp.notification.email.templating.exception.EmailTemplateNotFoundException;
import com.pedalbuildpipeline.pbp.notification.email.templating.model.EmailTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailTemplateRegistryTest {
  private EmailTemplateRegistry emailTemplateRegistry;

  @DisplayName(
      "given the registry contains a notification type, when retrieving the template, the template is returned")
  @Test
  public void doesReturnTemplateIfExists() throws EmailTemplateNotFoundException {
    EmailTemplate emailTemplate = new EmailTemplate(Template.EMPTY, Template.EMPTY);
    EmailTemplateRegistry emailTemplateRegistry =
        new EmailTemplateRegistry(Map.of(NotificationType.USER_REGISTRATION, emailTemplate));

    EmailTemplate retrievedTemplate =
        emailTemplateRegistry.getEmailTemplate(NotificationType.USER_REGISTRATION);

    assertThat(retrievedTemplate).isEqualTo(emailTemplate);
  }

  @DisplayName(
      "given the registry does not contain a notification type, when retrieving the template, then an exception is thrown")
  @Test
  public void doesThrowIfNotificationTypeNotInRegistry() {
    EmailTemplateRegistry emailTemplateRegistry = new EmailTemplateRegistry(Map.of());

    assertThrows(
        EmailTemplateNotFoundException.class,
        () -> emailTemplateRegistry.getEmailTemplate(NotificationType.USER_REGISTRATION));
  }
}
