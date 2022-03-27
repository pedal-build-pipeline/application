package com.pedalbuildpipeline.pbp.event.outbox.service;

import com.pedalbuildpipeline.pbp.event.outbox.annotation.OutboxProcessing;
import com.pedalbuildpipeline.pbp.event.outbox.model.TaskProcessingResult;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(value = "outbox.polling.enabled", havingValue = "true")
@Slf4j
@Component
public class OutboxPoller {
  private final OutboxProcessorService outboxProcessorService;
  private final AsyncTaskExecutor taskExecutor;
  private final long pollingIntervalMs;

  public OutboxPoller(
      OutboxProcessorService outboxProcessorService,
      @OutboxProcessing AsyncTaskExecutor taskExecutor,
      @Value("${outbox.polling.intervalMs}") long pollingIntervalMs) {
    this.outboxProcessorService = outboxProcessorService;
    this.taskExecutor = taskExecutor;
    this.pollingIntervalMs = pollingIntervalMs;
  }

  @Scheduled(fixedRateString = "${outbox.polling.intervalMs}")
  public void pollOutbox() {
    log.info("Begin polling outbox");

    long start = System.nanoTime();
    long now = System.nanoTime();
    boolean moreTasks = true;

    while (moreTasks && (now - start) > (pollingIntervalMs - 250)) {
      log.info("Continuing polling");
      Future<TaskProcessingResult> result =
          taskExecutor.submit(outboxProcessorService::processOutbox);

      try {
        moreTasks = result.get().getTasksProcessed() > 0;
      } catch (Exception e) {
        log.error("Error while polling events", e);
        moreTasks = false;
      }

      now = System.nanoTime();
    }

    log.info("Finished processing outbox");
  }
}
