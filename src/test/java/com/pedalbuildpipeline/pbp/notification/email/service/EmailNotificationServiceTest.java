package com.pedalbuildpipeline.pbp.notification.email.service;

import com.pedalbuildpipeline.pbp.notification.email.config.EmailNotificationsConfiguration;
import com.pedalbuildpipeline.pbp.notification.email.templating.EmailTemplatingService;
import com.pedalbuildpipeline.pbp.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
  public void sendNotificationUserEmailFailure() {}

  @DisplayName(
      "given a notification send request, when the request is submitted, then the email is sent, the details recorded, and returned")
  @Test
  public void sendNotificationDoesSendAndStoreDetailsAndReturn() {}
}
