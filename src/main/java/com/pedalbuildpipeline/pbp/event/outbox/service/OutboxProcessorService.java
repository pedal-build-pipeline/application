package com.pedalbuildpipeline.pbp.event.outbox.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedalbuildpipeline.pbp.event.EventTypeRegistry;
import com.pedalbuildpipeline.pbp.event.bus.EventBus;
import com.pedalbuildpipeline.pbp.event.exception.EventProcessingException;
import com.pedalbuildpipeline.pbp.event.exception.InvalidEventConfigurationException;
import com.pedalbuildpipeline.pbp.event.outbox.model.TaskProcessingResult;
import com.pedalbuildpipeline.pbp.event.outbox.repo.entity.OutboxEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceUnit;
import java.util.List;
import java.util.Optional;

/*
 * Heavily inspired by https://github.com/maciejtoporowicz/transactional-outbox-example
 */
@Service
public class OutboxProcessorService {
  @PersistenceUnit
  private EntityManagerFactory entityManagerFactory;
  private EventTypeRegistry eventTypeRegistry;
  private ObjectMapper objectMapper;
  private EventBus eventBus;

  @Autowired
  public void setEventTypeRegistry(EventTypeRegistry eventTypeRegistry) {
    this.eventTypeRegistry = eventTypeRegistry;
  }

  @Autowired
  public void setObjectMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Autowired
  public void setEventBus(EventBus eventBus) {
    this.eventBus = eventBus;
  }

  public static final String LOCK_FOR_PROCESSING_QUERY =
      "DELETE from outbox "
          + "WHERE id = ("
          + "SELECT id FROM outbox "
          + "ORDER BY insert_order ASC "
          + "FOR UPDATE SKIP LOCKED "
          + "LIMIT 1"
          + ") "
          + "RETURNING *;";

  public TaskProcessingResult processOutbox() {
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    EntityTransaction transaction = entityManager.getTransaction();
    transaction.begin();

    int tasksProcessed = 0;
    try {
      Optional<OutboxEntry> outboxEntry = holdFirstOutboxEntry(entityManager);
      if (outboxEntry.isPresent()) {
        ++tasksProcessed;
        eventBus.publish(parseEventPayload(outboxEntry.get()));
        transaction.commit();
      } else {
        transaction.rollback();
      }
    } catch (Throwable e) {
      try {
        transaction.rollback();
      } catch (Throwable rollbackException) {
        e.addSuppressed(rollbackException);
      }

      throw e;
    } finally {
      entityManager.close();
    }

    return new TaskProcessingResult(tasksProcessed);
  }

  private Object parseEventPayload(OutboxEntry outboxEntry) {
    try {
      return objectMapper.readValue(
          outboxEntry.getPayload(),
          eventTypeRegistry
              .getEventClassForType(outboxEntry.getEventType())
              .orElseThrow(
                  () ->
                      new InvalidEventConfigurationException(
                          "Attempting to deserialize event of type "
                              + outboxEntry.getEventType()
                              + " but found no event type mapping")));
    } catch (JsonProcessingException e) {
      throw new EventProcessingException("Unable to parse event payload for processing", e);
    }
  }

  @SuppressWarnings("unchecked")
  private Optional<OutboxEntry> holdFirstOutboxEntry(EntityManager entityManager) {
    Optional<OutboxEntry> result;

    // TODO: Skip certain events? Number of retries? What about "ordering" within an "aggregate"?
    List<OutboxEntry> outboxEntries =
        (List<OutboxEntry>)
            entityManager
                .createNativeQuery(LOCK_FOR_PROCESSING_QUERY, OutboxEntry.class)
                .getResultList();

    if (outboxEntries.size() == 1) {
      result = Optional.of(outboxEntries.get(0));
    } else if (outboxEntries.isEmpty()) {
      result = Optional.empty();
    } else {
      throw new IllegalStateException(
          "Invalid retrieval of outbox messages -- retrieved "
              + outboxEntries.size()
              + " messages.");
    }

    return result;
  }
}
