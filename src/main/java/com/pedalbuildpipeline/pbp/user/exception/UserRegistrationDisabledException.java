package com.pedalbuildpipeline.pbp.user.exception;

import java.net.URI;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

public class UserRegistrationDisabledException extends AbstractThrowableProblem {
  private static final URI TYPE =
      URI.create("https://app.pedalbuildpipeline.com/errors/username-registration-disabled");

  public UserRegistrationDisabledException() {
    super(
        TYPE,
        "User registration is presently disabled",
        Status.NOT_IMPLEMENTED,
        "User registration is not presently enabled, so new users cannot directly register");
  }
}
