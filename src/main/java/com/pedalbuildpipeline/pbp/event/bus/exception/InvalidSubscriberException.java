package com.pedalbuildpipeline.pbp.event.bus.exception;

public class InvalidSubscriberException extends RuntimeException {
  public InvalidSubscriberException(String message) {
    super(message);
  }

  public InvalidSubscriberException(String message, Throwable cause) {
    super(message, cause);
  }
}
