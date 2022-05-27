package com.pedalbuildpipeline.pbp.project.exception;

import lombok.Getter;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

@Getter
public class MediaStorageException extends AbstractThrowableProblem {
  private static final URI TYPE =
          URI.create("https://app.pedalbuildpipeline.com/errors/media-storage");

  private final String filename;

  public MediaStorageException(String filename) {
    super(
            TYPE,
            "Media could not be stored",
            Status.CONFLICT,
            String.format("Media with name '%s' could not be stored", filename));
    this.filename = filename;
  }
}
