package com.pedalbuildpipeline.pbp.notification.email.service;

import com.pedalbuildpipeline.pbp.notification.NotificationType;
import com.pedalbuildpipeline.pbp.notification.email.config.EmailNotificationsConfiguration;
import com.pedalbuildpipeline.pbp.notification.email.entity.SentEmailNotificationRecord;
import com.pedalbuildpipeline.pbp.notification.email.exception.UserEmailLookupException;
import com.pedalbuildpipeline.pbp.notification.email.templating.EmailTemplatingService;
import com.pedalbuildpipeline.pbp.notification.email.templating.exception.EmailTemplateNotFoundException;
import com.pedalbuildpipeline.pbp.notification.email.templating.exception.EmailTemplateRenderingException;
import com.pedalbuildpipeline.pbp.notification.model.NotificationDetails;
import com.pedalbuildpipeline.pbp.notification.model.NotificationRequest;
import com.pedalbuildpipeline.pbp.notification.model.email.EmailAddress;
import com.pedalbuildpipeline.pbp.notification.model.email.EmailBody;
import com.pedalbuildpipeline.pbp.user.repo.entity.User;
import com.pedalbuildpipeline.pbp.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailNotificationServiceTest {
  @InjectMocks public EmailNotificationService emailNotificationService;

  @Mock public EmailTemplatingService emailTemplatingService;

  @Mock public UserService userService;

  @Mock public EmailNotificationsConfiguration emailNotificationsConfiguration;

  @Mock public SentEmailNotificationService sentEmailNotificationService;

  @Mock public EmailNotificationSender emailNotificationSender;

  @DisplayName(
      "given a notification send request, if the user email cannot be found, then an exception is thrown")
  @Test
  public void sendNotificationUserEmailFailure() {
    UUID userId = UUID.randomUUID();

    when(userService.findUser(userId)).thenReturn(Optional.empty());

    assertThrows(
        UserEmailLookupException.class,
        () ->
            emailNotificationService.sendNotification(
                userId, new NotificationRequest(NotificationType.USER_REGISTRATION, Map.of())));

    verify(userService).findUser(userId);
  }

  @DisplayName(
      "given a notification send request, when the request is submitted, then the email is sent, the details recorded, and returned")
  @Test
  public void sendNotificationDoesSendAndStoreDetailsAndReturn()
      throws EmailTemplateRenderingException, EmailTemplateNotFoundException,
          UserEmailLookupException {
    UUID userId = UUID.randomUUID();
    User user = new User(userId, "test-user", "test@test.com", "****", new String[0], true);
    Map<String, Object> templateParameters = Map.of("test", "value", "other", "thing");
    NotificationDetails notificationDetails =
        new NotificationDetails("mailpace", "12345", "QUEUED", Map.of("some", "metadata"));
    SentEmailNotificationRecord sentEmailNotificationRecord =
        mock(SentEmailNotificationRecord.class);

    EmailNotificationsConfiguration.Addresses configuredAddresses =
        new EmailNotificationsConfiguration.Addresses();
    EmailNotificationsConfiguration.EmailAddress fromAddress =
        new EmailNotificationsConfiguration.EmailAddress();
    fromAddress.setAddress("pbp@test.com");
    fromAddress.setName("PBP");
    configuredAddresses.setFrom(fromAddress);
    EmailNotificationsConfiguration.EmailAddress replyAddress =
        new EmailNotificationsConfiguration.EmailAddress();
    replyAddress.setAddress("no-reply@test.com");
    replyAddress.setName("no-reply");
    configuredAddresses.setReplyTo(replyAddress);

    when(userService.findUser(userId)).thenReturn(Optional.of(user));
    when(emailTemplatingService.renderEmailBodyForNotificationType(
            NotificationType.USER_REGISTRATION, templateParameters))
        .thenReturn(new EmailBody("<html><body>Hello world</body></html>", "Hello world"));
    when(emailNotificationsConfiguration.getAddresses()).thenReturn(configuredAddresses);

    when(emailNotificationSender.doSend(
            argThat(
                (address) -> address.name().isEmpty() && "test@test.com".equals(address.address())),
            argThat(
                (address) ->
                    Optional.of("PBP").equals(address.name())
                        && "pbp@test.com".equals(address.address())),
            argThat(
                (optionalAddress) ->
                    optionalAddress.isPresent()
                        && Optional.of("no-reply").equals(optionalAddress.get().name())
                        && "no-reply@test.com".equals(optionalAddress.get().address())),
            argThat(
                (emailBody) ->
                    "<html><body>Hello world</body></html>".equals(emailBody.htmlBody())
                        && "Hello world".equals(emailBody.textBody()))))
        .thenReturn(notificationDetails);

    when(sentEmailNotificationService.saveNotificationDetails(userId, notificationDetails))
        .thenReturn(sentEmailNotificationRecord);

    NotificationDetails returnedNotificationDetails =
        emailNotificationService.sendNotification(
            userId,
            new NotificationRequest(NotificationType.USER_REGISTRATION, templateParameters));

    assertThat(returnedNotificationDetails).isEqualTo(notificationDetails);

    verify(emailNotificationSender)
        .doSend(
            argThat(
                (address) -> address.name().isEmpty() && "test@test.com".equals(address.address())),
            argThat(
                (address) ->
                    Optional.of("PBP").equals(address.name())
                        && "pbp@test.com".equals(address.address())),
            argThat(
                (optionalAddress) ->
                    optionalAddress.isPresent()
                        && Optional.of("no-reply").equals(optionalAddress.get().name())
                        && "no-reply@test.com".equals(optionalAddress.get().address())),
            argThat(
                (emailBody) ->
                    "<html><body>Hello world</body></html>".equals(emailBody.htmlBody())
                        && "Hello world".equals(emailBody.textBody())));

    verify(sentEmailNotificationService).saveNotificationDetails(userId, notificationDetails);
  }
}
