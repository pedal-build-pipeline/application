package com.pedalbuildpipeline.pbp.notification.email.templating;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.github.jknack.handlebars.Template;
import com.pedalbuildpipeline.pbp.notification.NotificationType;
import com.pedalbuildpipeline.pbp.notification.email.templating.exception.EmailTemplateNotFoundException;
import com.pedalbuildpipeline.pbp.notification.email.templating.exception.EmailTemplateRenderingException;
import com.pedalbuildpipeline.pbp.notification.email.templating.model.EmailTemplate;
import com.pedalbuildpipeline.pbp.notification.model.email.EmailBody;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmailTemplatingServiceTest {
  @InjectMocks private EmailTemplatingService emailTemplatingService;

  @Mock private EmailTemplateRegistry emailTemplateRegistry;

  @DisplayName(
      "given a template exists for a notification type, when the template is rendered, then the body is returned")
  @Test
  public void renderEmailSuccess()
      throws EmailTemplateNotFoundException, IOException, EmailTemplateRenderingException {
    Template htmlTemplate = mock(Template.class);
    Template textTemplate = mock(Template.class);

    when(emailTemplateRegistry.getEmailTemplate(NotificationType.USER_REGISTRATION))
        .thenReturn(new EmailTemplate(htmlTemplate, textTemplate));

    when(htmlTemplate.apply(Map.of("test", "value"))).thenReturn("html");
    when(textTemplate.apply(Map.of("test", "value"))).thenReturn("text");

    EmailBody renderedBody =
        emailTemplatingService.renderEmailBodyForNotificationType(
            NotificationType.USER_REGISTRATION, Map.of("test", "value"));

    assertThat(renderedBody.htmlBody()).isEqualTo("html");
    assertThat(renderedBody.textBody()).isEqualTo("text");

    verify(htmlTemplate).apply(Map.of("test", "value"));
    verify(textTemplate).apply(Map.of("test", "value"));
  }

  @DisplayName(
      "given a template exists for a notification type but fails during render, when the template is rendered, then an exception is thrown")
  @Test
  public void ioExceptionDuringRendering() throws EmailTemplateNotFoundException, IOException {
    Template htmlTemplate = mock(Template.class);
    Template textTemplate = mock(Template.class);

    when(emailTemplateRegistry.getEmailTemplate(NotificationType.USER_REGISTRATION))
        .thenReturn(new EmailTemplate(htmlTemplate, textTemplate));

    when(htmlTemplate.apply(Map.of("test", "value"))).thenReturn("html");
    when(textTemplate.apply(Map.of("test", "value")))
        .thenThrow(new IOException("Unable to render template"));

    assertThrows(
        EmailTemplateRenderingException.class,
        () ->
            emailTemplatingService.renderEmailBodyForNotificationType(
                NotificationType.USER_REGISTRATION, Map.of("test", "value")));

    verify(htmlTemplate).apply(Map.of("test", "value"));
    verify(textTemplate).apply(Map.of("test", "value"));
  }
}
