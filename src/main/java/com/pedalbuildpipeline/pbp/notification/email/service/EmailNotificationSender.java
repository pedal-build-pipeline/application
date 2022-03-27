package com.pedalbuildpipeline.pbp.notification.email.service;

import com.pedalbuildpipeline.pbp.notification.model.NotificationDetails;
import com.pedalbuildpipeline.pbp.notification.model.email.EmailAddress;
import com.pedalbuildpipeline.pbp.notification.model.email.EmailBody;

import java.util.Optional;

public interface EmailNotificationSender {
  NotificationDetails doSend(
      EmailAddress to, EmailAddress from, Optional<EmailAddress> replyTo, EmailBody emailBody);
}
