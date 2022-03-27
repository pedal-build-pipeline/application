package com.pedalbuildpipeline.pbp.notification.email.mailpace;

import com.pedalbuildpipeline.pbp.AppConstants;
import com.pedalbuildpipeline.pbp.notification.email.mailpace.model.SendEmailRequest;
import com.pedalbuildpipeline.pbp.notification.email.mailpace.model.SendEmailResponse;
import com.pedalbuildpipeline.pbp.notification.email.mailpace.api.MailPaceV1API;
import com.pedalbuildpipeline.pbp.notification.email.service.EmailNotificationSender;
import com.pedalbuildpipeline.pbp.notification.model.NotificationDetails;
import com.pedalbuildpipeline.pbp.notification.model.email.EmailAddress;
import com.pedalbuildpipeline.pbp.notification.model.email.EmailBody;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@ConditionalOnProperty(
    value = "notifications.email.provider",
    havingValue = AppConstants.Notifications.Email.MAILPACE)
@RequiredArgsConstructor
public class MailPaceEmailNotificationSender implements EmailNotificationSender {
  private final MailPaceV1API mailPaceV1API;

  @Override
  public NotificationDetails doSend(
      EmailAddress to, EmailAddress from, Optional<EmailAddress> replyTo, EmailBody emailBody) {
    SendEmailResponse sendEmailResponse =
        mailPaceV1API.sendEmail(
            SendEmailRequest.builder()
                .to(Set.of(to.toRFC822()))
                .from(from.toRFC822())
                .replyto(replyTo.map(EmailAddress::toRFC822).orElse(null))
                .htmlbody(emailBody.htmlBody())
                .textbody(emailBody.textBody())
                .build());

    return new NotificationDetails(
        AppConstants.Notifications.Email.MAILPACE,
        String.valueOf(sendEmailResponse.getId()),
        sendEmailResponse.getStatus(),
        Map.of());
  }
}
