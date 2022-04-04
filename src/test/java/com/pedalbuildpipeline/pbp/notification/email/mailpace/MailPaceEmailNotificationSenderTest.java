package com.pedalbuildpipeline.pbp.notification.email.mailpace;

import com.pedalbuildpipeline.pbp.AppConstants;
import com.pedalbuildpipeline.pbp.notification.email.mailpace.api.MailPaceV1API;
import com.pedalbuildpipeline.pbp.notification.email.mailpace.model.SendEmailRequest;
import com.pedalbuildpipeline.pbp.notification.email.mailpace.model.SendEmailResponse;
import com.pedalbuildpipeline.pbp.notification.model.NotificationDetails;
import com.pedalbuildpipeline.pbp.notification.model.email.EmailAddress;
import com.pedalbuildpipeline.pbp.notification.model.email.EmailBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MailPaceEmailNotificationSenderTest {
  @InjectMocks public MailPaceEmailNotificationSender sender;

  @Mock public MailPaceV1API mailPaceV1API;

  @DisplayName(
      "given an email send request, when sending the email, the request is translated, sent, and details returned")
  @Test
  public void doSendDoesSendTranslatedRequestAndReturnDetails() {
    SendEmailResponse response = SendEmailResponse.builder().id(1234).status("QUEUED").build();

    ArgumentCaptor<SendEmailRequest> requestArgumentCaptor =
        ArgumentCaptor.forClass(SendEmailRequest.class);
    when(mailPaceV1API.sendEmail(requestArgumentCaptor.capture())).thenReturn(response);

    EmailAddress to = new EmailAddress(Optional.of("To Person"), "to@test.com");
    EmailAddress from = new EmailAddress(Optional.of("From Person"), "from@test.com");
    Optional<EmailAddress> replyTo =
        Optional.of(new EmailAddress(Optional.of("Reply Person"), "reply@test.com"));
    EmailBody emailBody = new EmailBody("HTML Body", "Text Body");
    NotificationDetails notificationDetails = sender.doSend(to, from, replyTo, emailBody);

    assertThat(notificationDetails.id()).isEqualTo("1234");
    assertThat(notificationDetails.status()).isEqualTo("QUEUED");
    assertThat(notificationDetails.provider()).isEqualTo(AppConstants.Notifications.Email.MAILPACE);

    assertThat(requestArgumentCaptor.getAllValues().size()).isEqualTo(1);
    SendEmailRequest request = requestArgumentCaptor.getValue();
    assertThat(request.getTo()).isEqualTo(Set.of("To Person <to@test.com>"));
    assertThat(request.getFrom()).isEqualTo("From Person <from@test.com>");
    assertThat(request.getReplyto()).isEqualTo("Reply Person <reply@test.com>");
    assertThat(request.getHtmlbody()).isEqualTo("HTML Body");
    assertThat(request.getTextbody()).isEqualTo("Text Body");
  }
}
