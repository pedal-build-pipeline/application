package com.pedalbuildpipeline.pbp.notification.email.service;

import com.pedalbuildpipeline.pbp.notification.NotificationService;
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
import java.util.Optional;
import java.util.UUID;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationService implements NotificationService {
  private final EmailTemplatingService emailTemplatingService;
  private final UserService userService;
  private final EmailNotificationsConfiguration emailNotificationsConfiguration;
  private final SentEmailNotificationService sentEmailNotificationService;
  private final EmailNotificationSender emailNotificationSender;

  @Override
  public NotificationDetails sendNotification(UUID userId, NotificationRequest notificationRequest)
      throws UserEmailLookupException, EmailTemplateNotFoundException,
          EmailTemplateRenderingException {
    User user =
        userService
            .findUser(userId)
            .orElseThrow(
                () ->
                    new UserEmailLookupException(
                        "Unable to find user with provided id " + userId.toString(), userId));

    EmailBody emailBody =
        emailTemplatingService.renderEmailBodyForNotificationType(
            notificationRequest.notificationType(), notificationRequest.templateParameters());

    log.info(
        "Attempting to send notification of type {} to user with id {}",
        notificationRequest.notificationType(),
        user.getId());

    NotificationDetails notificationDetails =
        emailNotificationSender.doSend(
            new EmailAddress(Optional.empty(), user.getEmail()),
            new EmailAddress(
                Optional.ofNullable(
                    emailNotificationsConfiguration.getAddresses().getFrom().getName()),
                emailNotificationsConfiguration.getAddresses().getFrom().getAddress()),
            Optional.ofNullable(emailNotificationsConfiguration.getAddresses().getReplyTo())
                .map(
                    (emailAddress) ->
                        new EmailAddress(
                            Optional.ofNullable(emailAddress.getName()),
                            emailAddress.getAddress())),
            emailBody);

    SentEmailNotificationRecord savedRecord =
        sentEmailNotificationService.saveNotificationDetails(user.getId(), notificationDetails);

    log.info(
        "Sent notification with id {} using provider {} and provider id {}",
        savedRecord.getId(),
        savedRecord.getProvider(),
        savedRecord.getProviderId());

    return notificationDetails;
  }
}
