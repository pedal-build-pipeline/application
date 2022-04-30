package com.pedalbuildpipeline.pbp.event.listener;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pedalbuildpipeline.pbp.event.exception.EventProcessingException;
import com.pedalbuildpipeline.pbp.event.model.user.UserCreatedEvent;
import com.pedalbuildpipeline.pbp.notification.NotificationService;
import com.pedalbuildpipeline.pbp.notification.NotificationType;
import com.pedalbuildpipeline.pbp.notification.email.exception.UserEmailLookupException;
import com.pedalbuildpipeline.pbp.notification.email.templating.exception.EmailTemplateNotFoundException;
import com.pedalbuildpipeline.pbp.notification.email.templating.exception.EmailTemplateRenderingException;
import com.pedalbuildpipeline.pbp.notification.model.NotificationDetails;
import com.pedalbuildpipeline.pbp.notification.model.NotificationRequest;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserCreatedEventListenerTest {
  @Mock private NotificationService notificationService;

  @InjectMocks private UserCreatedEventListener userCreatedEventListener;

  @DisplayName(
      "given a user created event, when the listener is called, then the proper notification is sent")
  @Test
  public void successfullyHandleEvent()
      throws EmailTemplateRenderingException, EmailTemplateNotFoundException,
          UserEmailLookupException {
    UUID userId = UUID.randomUUID();
    UserCreatedEvent userCreatedEvent = new UserCreatedEvent(userId, "username");

    UUID notificationId = UUID.randomUUID();
    NotificationDetails notificationDetails =
        new NotificationDetails("mailpace", notificationId.toString(), "QUEUED", Map.of());

    when(notificationService.sendNotification(eq(userId), any())).thenReturn(notificationDetails);

    userCreatedEventListener.consume(userCreatedEvent);

    ArgumentCaptor<NotificationRequest> notificationRequestArgumentCaptor =
        ArgumentCaptor.forClass(NotificationRequest.class);
    verify(notificationService)
        .sendNotification(eq(userId), notificationRequestArgumentCaptor.capture());

    assertThat(notificationRequestArgumentCaptor.getAllValues().size()).isEqualTo(1);
    assertThat(notificationRequestArgumentCaptor.getValue().notificationType())
        .isEqualTo(NotificationType.USER_REGISTRATION);
    assertThat(notificationRequestArgumentCaptor.getValue().templateParameters())
        .isEqualTo(Map.of("username", "username"));
  }

  @DisplayName(
      "given a user created event, when the listener is called and an exception occurs during notification send, then the proper exception is thrown")
  @ParameterizedTest(name = "{index} ==> exception of type {0}")
  @MethodSource("notificationExceptions")
  public void errorDuringEventHandling(Exception e)
      throws EmailTemplateRenderingException, EmailTemplateNotFoundException,
          UserEmailLookupException {
    UUID userId = UUID.randomUUID();
    UserCreatedEvent userCreatedEvent = new UserCreatedEvent(userId, "username");

    when(notificationService.sendNotification(eq(userId), any())).thenThrow(e);

    assertThrows(
        EventProcessingException.class, () -> userCreatedEventListener.consume(userCreatedEvent));

    ArgumentCaptor<NotificationRequest> notificationRequestArgumentCaptor =
        ArgumentCaptor.forClass(NotificationRequest.class);
    verify(notificationService)
        .sendNotification(eq(userId), notificationRequestArgumentCaptor.capture());

    assertThat(notificationRequestArgumentCaptor.getAllValues().size()).isEqualTo(1);
    assertThat(notificationRequestArgumentCaptor.getValue().notificationType())
        .isEqualTo(NotificationType.USER_REGISTRATION);
    assertThat(notificationRequestArgumentCaptor.getValue().templateParameters())
        .isEqualTo(Map.of("username", "username"));
  }

  public static Stream<Arguments> notificationExceptions() {
    return Stream.of(
        Arguments.of(
            Named.of(
                "UserEmailLookupException",
                new UserEmailLookupException("Unable to find user email", UUID.randomUUID()))),
        Arguments.of(
            Named.of(
                "EmailTemplateNotFoundException",
                new EmailTemplateNotFoundException(
                    "Unable to find template", NotificationType.USER_REGISTRATION))),
        Arguments.of(
            Named.of(
                "EmailTemplateRenderingException",
                new EmailTemplateRenderingException(
                    "Unable to render template",
                    new RuntimeException(),
                    NotificationType.USER_REGISTRATION))));
  }
}
