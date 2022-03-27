package com.pedalbuildpipeline.pbp.event.listener;

import com.pedalbuildpipeline.pbp.event.bus.EventListener;
import com.pedalbuildpipeline.pbp.event.model.user.UserCreatedEvent;
import com.pedalbuildpipeline.pbp.notification.NotificationService;
import com.pedalbuildpipeline.pbp.notification.NotificationType;
import com.pedalbuildpipeline.pbp.notification.email.exception.UserEmailLookupException;
import com.pedalbuildpipeline.pbp.notification.email.templating.exception.EmailTemplateNotFoundException;
import com.pedalbuildpipeline.pbp.notification.email.templating.exception.EmailTemplateRenderingException;
import com.pedalbuildpipeline.pbp.notification.model.NotificationDetails;
import com.pedalbuildpipeline.pbp.notification.model.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserCreatedEventListener implements EventListener<UserCreatedEvent> {
  private final NotificationService notificationService;

  @Override
  public void consume(UserCreatedEvent event) {
    // TODO: Idempotency of some kind -- perhaps a timeout on resend?
    log.info("Processing user created event for user with id {}", event.getAggregateId());
    try {
      NotificationDetails notificationDetails =
          notificationService.sendNotification(
              UUID.fromString(event.getAggregateId()),
              new NotificationRequest(NotificationType.USER_REGISTRATION, Map.of()));

      log.info("Successfully sent notification with id {}", notificationDetails.id());
    } catch (UserEmailLookupException
        | EmailTemplateNotFoundException
        | EmailTemplateRenderingException e) {
      log.error("Unable to send notification due to exception", e);
    }
  }
}
