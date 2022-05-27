package com.pedalbuildpipeline.pbp.event.outbox.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedalbuildpipeline.pbp.event.EventTypeRegistry;
import com.pedalbuildpipeline.pbp.event.bus.EventBus;
import com.pedalbuildpipeline.pbp.event.exception.EventProcessingException;
import com.pedalbuildpipeline.pbp.event.exception.InvalidEventConfigurationException;
import com.pedalbuildpipeline.pbp.user.event.UserCreatedEvent;
import com.pedalbuildpipeline.pbp.event.outbox.model.TaskProcessingResult;
import com.pedalbuildpipeline.pbp.event.outbox.repo.entity.OutboxEntry;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OutboxProcessorServiceTest {
  @InjectMocks private OutboxProcessorService outboxProcessorService;

  @Mock private EntityManagerFactory entityManagerFactory;

  @Mock private EntityManager entityManager;

  @Mock private EventTypeRegistry eventTypeRegistry;

  @Mock private ObjectMapper objectMapper;

  @Mock private EventBus eventBus;

  @Test
  public void failsIfMultipleResultsComeBackFromDb() {
    EntityTransaction transaction = mock(EntityTransaction.class);
    Query query = mock(Query.class);
    OutboxEntry outboxEntry1 = OutboxEntry.builder().build();
    OutboxEntry outboxEntry2 = OutboxEntry.builder().build();

    when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
    when(entityManager.getTransaction()).thenReturn(transaction);
    when(entityManager.createNativeQuery(
            OutboxProcessorService.LOCK_FOR_PROCESSING_QUERY, OutboxEntry.class))
        .thenReturn(query);
    when(query.getResultList()).thenReturn(List.of(outboxEntry1, outboxEntry2));

    assertThrows(IllegalStateException.class, () -> outboxProcessorService.processOutbox());

    verify(transaction).rollback();
    verify(entityManager).close();
  }

  @Test
  public void zeroResultsIfNoRecordsFound() {
    EntityTransaction transaction = mock(EntityTransaction.class);
    Query query = mock(Query.class);

    when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
    when(entityManager.getTransaction()).thenReturn(transaction);
    when(entityManager.createNativeQuery(
            OutboxProcessorService.LOCK_FOR_PROCESSING_QUERY, OutboxEntry.class))
        .thenReturn(query);
    when(query.getResultList()).thenReturn(List.of());

    TaskProcessingResult result = outboxProcessorService.processOutbox();

    assertThat(result.tasksProcessed()).isEqualTo(0);

    verify(transaction).rollback();
    verify(entityManager).close();
  }

  @Test
  public void failsIfEventClassNotFound() {
    EntityTransaction transaction = mock(EntityTransaction.class);
    Query query = mock(Query.class);
    OutboxEntry outboxEntry = OutboxEntry.builder().eventType("TEST_TYPE").payload("{").build();

    when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
    when(entityManager.getTransaction()).thenReturn(transaction);
    when(entityManager.createNativeQuery(
            OutboxProcessorService.LOCK_FOR_PROCESSING_QUERY, OutboxEntry.class))
        .thenReturn(query);
    when(query.getResultList()).thenReturn(List.of(outboxEntry));
    when(eventTypeRegistry.getEventClassForType("TEST_TYPE")).thenReturn(Optional.empty());

    assertThrows(
        InvalidEventConfigurationException.class, () -> outboxProcessorService.processOutbox());

    verify(transaction).rollback();
    verify(entityManager).close();
  }

  @Test
  public void failsIfEventPayloadParsingFails() throws JsonProcessingException {
    EntityTransaction transaction = mock(EntityTransaction.class);
    Query query = mock(Query.class);
    OutboxEntry outboxEntry = OutboxEntry.builder().eventType("TEST_TYPE").payload("{").build();
    JsonMappingException exception = mock(JsonMappingException.class);

    when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
    when(entityManager.getTransaction()).thenReturn(transaction);
    when(entityManager.createNativeQuery(
            OutboxProcessorService.LOCK_FOR_PROCESSING_QUERY, OutboxEntry.class))
        .thenReturn(query);
    when(query.getResultList()).thenReturn(List.of(outboxEntry));
    when(eventTypeRegistry.getEventClassForType("TEST_TYPE"))
        .thenReturn(Optional.of(UserCreatedEvent.class));
    when(objectMapper.readValue("{", UserCreatedEvent.class)).thenThrow(exception);

    assertThrows(EventProcessingException.class, () -> outboxProcessorService.processOutbox());

    verify(transaction).rollback();
    verify(entityManager).close();
  }

  @Test
  public void failsIfBusPublishThrows() {}

  public void publishesIfSingleEventFoundAndParsed() {}
}
