package com.pedalbuildpipeline.pbp.event.outbox.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TaskProcessingResult {
  private final long tasksProcessed;
}
