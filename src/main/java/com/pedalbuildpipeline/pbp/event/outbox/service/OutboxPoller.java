package com.pedalbuildpipeline.pbp.event.outbox.service;

import com.pedalbuildpipeline.pbp.event.outbox.model.TaskProcessingResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

@ConditionalOnProperty(value = "outbox.polling.enabled", havingValue = "true")
@Slf4j
@Component
public class OutboxPoller {
  private final OutboxProcessorService outboxProcessorService;
  private final Clock clock;
  private final long pollingIntervalMs;

  public OutboxPoller(
      OutboxProcessorService outboxProcessorService,
      Clock clock,
      @Value("${outbox.polling.intervalMs}") long pollingIntervalMs) {
    this.outboxProcessorService = outboxProcessorService;
    this.clock = clock;
    this.pollingIntervalMs = pollingIntervalMs;
  }

  @Scheduled(fixedRateString = "${outbox.polling.intervalMs}")
  public void pollOutbox() {
    log.info("Begin polling outbox");

    Instant start = Instant.now(clock);
    Instant now = Instant.now(clock);
    boolean moreTasks = true;

    long totalTasks = 0;
    while (moreTasks && Duration.between(start, now).toMillis() < (pollingIntervalMs - 250)) {
      log.info("Continuing polling");

      try {
        TaskProcessingResult result = outboxProcessorService.processOutbox();

        moreTasks = result.tasksProcessed() > 0;
        totalTasks += result.tasksProcessed();
      } catch (Exception e) {
        // TODO: Something more robust, but we'll have to be able to tell when it's safe to proceed
        log.error("Error while polling events", e);
        moreTasks = false;
      }

      now = Instant.now(clock);
    }

    log.info("Finished processing outbox. Total number of tasks processed: {}", totalTasks);
  }
}
