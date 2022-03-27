package com.pedalbuildpipeline.pbp.notification.email.config;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "notifications.email")
@Component
@Valid
@Data
public class EmailNotificationsConfiguration {
  @NotEmpty private String provider;

  @NotNull private Addresses addresses;

  @Data
  public static final class Addresses {
    @NotNull private EmailAddress from;
    private EmailAddress replyTo;
  }

  @Data
  public static final class EmailAddress {
    private String name;
    @NotEmpty private String address;
  }
}
