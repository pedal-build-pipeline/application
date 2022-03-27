package com.pedalbuildpipeline.pbp.notification;

import com.pedalbuildpipeline.pbp.notification.email.exception.UserEmailLookupException;
import com.pedalbuildpipeline.pbp.notification.email.templating.exception.EmailTemplateNotFoundException;
import com.pedalbuildpipeline.pbp.notification.email.templating.exception.EmailTemplateRenderingException;
import com.pedalbuildpipeline.pbp.notification.model.NotificationDetails;
import com.pedalbuildpipeline.pbp.notification.model.NotificationRequest;

import java.util.UUID;

public interface NotificationService {
  NotificationDetails sendNotification(UUID userId, NotificationRequest notificationRequest)
          throws UserEmailLookupException, EmailTemplateNotFoundException, EmailTemplateRenderingException;
}
