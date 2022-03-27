package com.pedalbuildpipeline.pbp.notification;

public enum NotificationType {
  USER_REGISTRATION("user_registration");

  private final String tag;

  NotificationType(String tag) {
    this.tag = tag;
  }

  public String getTag() {
    return tag;
  }
}
