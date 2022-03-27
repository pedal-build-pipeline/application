package com.pedalbuildpipeline.pbp.web.exception;

import com.pedalbuildpipeline.pbp.ResourceType;
import java.net.URI;
import lombok.Getter;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

@Getter
public class ResourceNotFoundException extends AbstractThrowableProblem {
  private static final URI TYPE =
      URI.create("https://app.pedalbuildpipeline.com/errors/resource-not-found");

  public ResourceNotFoundException(ResourceType resourceType, String resourceId) {
    super(
        TYPE,
        "The given resource was not found",
        Status.NOT_FOUND,
        String.format(
            "Resource of type '%s' with id '%s' was not found", resourceType.name(), resourceId));
  }
}
