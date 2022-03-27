package com.pedalbuildpipeline.pbp.notification.email.templating.config;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.pedalbuildpipeline.pbp.notification.NotificationType;
import com.pedalbuildpipeline.pbp.notification.email.templating.model.EmailTemplate;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailTemplateConfig {
  @Bean
  public Map<NotificationType, EmailTemplate> emailTemplateByNotificationType() throws IOException {
    TemplateLoader templateLoader = new ClassPathTemplateLoader();
    templateLoader.setPrefix("/templates/email");

    Handlebars handlebars = new Handlebars(templateLoader);

    Map<NotificationType, EmailTemplate> templateByNotificationType = new HashMap<>();
    for (NotificationType notificationType : NotificationType.values()) {
      templateByNotificationType.put(
          notificationType,
          new EmailTemplate(
              templateForType(handlebars, notificationType, "html"),
              templateForType(handlebars, notificationType, "text")));
    }

    return templateByNotificationType;
  }

  protected Template templateForType(
      Handlebars handlebars, NotificationType notificationType, String type) throws IOException {
    return handlebars.compile(notificationType.name() + "." + type);
  }
}
