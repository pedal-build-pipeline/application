package com.pedalbuildpipeline.pbp.event.outbox.service;

import static org.mockito.Mockito.*;

import com.pedalbuildpipeline.pbp.event.outbox.model.TaskProcessingResult;
import java.time.Clock;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OutboxPollerTest {
  private OutboxPoller outboxPoller;

  @Mock private Clock clock;

  @Mock private OutboxProcessorService outboxProcessorService;

  @BeforeEach
  public void setup() {
    outboxPoller = new OutboxPoller(outboxProcessorService, clock, 3000L);
  }

  @DisplayName(
      "given a set number of tasks to process, if tasks finish within allotted time, then the processing stops")
  @Test
  public void pollerDoesStopWhenNoMoreTasksToProcess() {
    when(clock.instant())
        .thenReturn(Instant.ofEpochSecond(1648732602))
        .thenReturn(Instant.ofEpochSecond(1648732602));

    when(outboxProcessorService.processOutbox())
        .thenReturn(
            new TaskProcessingResult(1),
            new TaskProcessingResult(1),
            new TaskProcessingResult(1),
            new TaskProcessingResult(1),
            new TaskProcessingResult(1),
            new TaskProcessingResult(0));

    outboxPoller.pollOutbox();

    verify(outboxProcessorService, times(6)).processOutbox();
  }

  @DisplayName(
      "given a set number of tasks to process, if tasks do not finish within allotted time, then processing stops short of completion")
  @Test
  public void pollerDoesStopWhenAllottedTimeEnds() {
    when(clock.instant())
        .thenReturn(Instant.ofEpochSecond(1648732602))
        .thenReturn(Instant.ofEpochSecond(1648732602))
        .thenReturn(Instant.ofEpochSecond(1648732602).plusSeconds(1))
        .thenReturn(Instant.ofEpochSecond(1648732602).plusSeconds(2))
        .thenReturn(Instant.ofEpochSecond(1648732602).plusSeconds(3))
        .thenReturn(Instant.ofEpochSecond(1648732602).plusSeconds(4));

    when(outboxProcessorService.processOutbox())
        .thenReturn(
            new TaskProcessingResult(1), new TaskProcessingResult(1), new TaskProcessingResult(1));

    outboxPoller.pollOutbox();

    verify(outboxProcessorService, times(3)).processOutbox();
  }

  @DisplayName(
      "given a set number of tasks to process, if a task throws an exception, then processing stops short of completion")
  @Test
  public void pollerDoesStopWhenExceptionOccurs() {
    when(clock.instant())
        .thenReturn(Instant.ofEpochSecond(1648732602))
        .thenReturn(Instant.ofEpochSecond(1648732602));

    when(outboxProcessorService.processOutbox())
        .thenReturn(new TaskProcessingResult(1), new TaskProcessingResult(1))
        .thenThrow(new RuntimeException("Something bad"));

    outboxPoller.pollOutbox();

    verify(outboxProcessorService, times(3)).processOutbox();
  }
}
