package com.pedalbuildpipeline.pbp.notification.model;

import com.pedalbuildpipeline.pbp.notification.NotificationType;
import java.util.Map;

public record NotificationRequest(
    NotificationType notificationType, Map<String, Object> templateParameters) {}
