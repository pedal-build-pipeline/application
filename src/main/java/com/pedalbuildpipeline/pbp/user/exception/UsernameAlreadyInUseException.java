package com.pedalbuildpipeline.pbp.user.exception;

import java.net.URI;
import lombok.Getter;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

@Getter
public class UsernameAlreadyInUseException extends AbstractThrowableProblem {
  private static final URI TYPE =
      URI.create("https://app.pedalbuildpipeline.com/errors/username-in-use");

  private final String requestedUsername;

  public UsernameAlreadyInUseException(String requestedUsername) {
    super(
        TYPE,
        "Username already in use",
        Status.CONFLICT,
        String.format("Username '%s' is already in use", requestedUsername));
    this.requestedUsername = requestedUsername;
  }
}
