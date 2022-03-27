package com.pedalbuildpipeline.pbp.features.toggles;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.Valid;

@Component
@ConfigurationProperties("features.toggles.users")
@Valid
@Data
public class UserFeatureToggles {
  private boolean registrationEnabled;
}
