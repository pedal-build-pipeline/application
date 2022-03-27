package com.pedalbuildpipeline.pbp.features.toggles;

import javax.validation.Valid;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("features.toggles.users")
@Valid
@Data
public class UserFeatureToggles {
  private boolean registrationEnabled;
}
