package com.pedalbuildpipeline.pbp.event.outbox.exception;

public class EventStorageException extends RuntimeException {
  public EventStorageException(String message, Throwable cause) {
    super(message, cause);
  }
}
