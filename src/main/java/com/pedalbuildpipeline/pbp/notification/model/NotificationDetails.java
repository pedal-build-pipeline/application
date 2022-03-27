package com.pedalbuildpipeline.pbp.notification.model;

import java.util.Map;

public record NotificationDetails(
    String provider, String id, String status, Map<String, Object> metadata) {}
