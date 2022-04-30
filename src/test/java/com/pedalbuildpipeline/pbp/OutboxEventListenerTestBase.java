package com.pedalbuildpipeline.pbp;

import com.pedalbuildpipeline.pbp.event.model.BaseEvent;
import com.pedalbuildpipeline.pbp.event.outbox.repo.OutboxRepository;
import com.pedalbuildpipeline.pbp.event.outbox.repo.entity.OutboxEntry;
import com.pedalbuildpipeline.pbp.event.outbox.service.OutboxPoller;
import com.pedalbuildpipeline.pbp.event.outbox.service.OutboxProcessorService;
import com.pedalbuildpipeline.pbp.event.outbox.service.OutboxService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.swing.text.html.parser.Entity;
import javax.transaction.TransactionManager;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OutboxEventListenerTestBase extends ComponentTestBase {
  protected OutboxPoller outboxPoller;

  protected Clock clock;

  @Autowired protected OutboxProcessorService outboxProcessorService;

  @Autowired protected OutboxService outboxService;

  @Autowired protected OutboxRepository outboxRepository;

  @Autowired protected EntityManager entityManager;

  @Autowired protected PlatformTransactionManager platformTransactionManager;

  protected TransactionTemplate transactionTemplate;

  @BeforeEach
  public void createPoller() {
    transactionTemplate = new TransactionTemplate(platformTransactionManager);

    clock = mock(Clock.class);
    Instant now = Instant.now();
    when(clock.instant()).thenReturn(now).thenReturn(now.minusSeconds(5));

    outboxPoller = new OutboxPoller(outboxProcessorService, clock, 3000);
  }

  @BeforeEach
  @AfterEach
  public void clearOutbox() {
    outboxRepository.deleteAll();
  }

  protected void executeWithEvents(List<BaseEvent> events) {
    try {
      transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
      transactionTemplate.execute(
          (status) -> {
            events.forEach((event) -> outboxService.createEntry(event));
            entityManager.flush();

            return null;
          });

      outboxPoller.pollOutbox();
    } catch (Throwable e) {
      outboxRepository.deleteAll();
      throw new RuntimeException(e);
    }
  }

  protected void executeWithEntries(List<OutboxEntry> outboxEntries) {
    try {
      transactionTemplate.execute(
          (status) -> {
            outboxRepository.saveAll(outboxEntries);
            entityManager.flush();

            return null;
          });

      outboxPoller.pollOutbox();
    } catch (Throwable e) {
      outboxRepository.deleteAll();
      throw new RuntimeException(e);
    }
  }
}
