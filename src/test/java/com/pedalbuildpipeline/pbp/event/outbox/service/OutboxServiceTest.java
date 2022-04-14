package com.pedalbuildpipeline.pbp.event.outbox.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedalbuildpipeline.pbp.event.AggregateType;
import com.pedalbuildpipeline.pbp.event.EventType;
import com.pedalbuildpipeline.pbp.event.model.user.UserCreatedEvent;
import com.pedalbuildpipeline.pbp.event.outbox.exception.EventStorageException;
import com.pedalbuildpipeline.pbp.event.outbox.repo.OutboxRepository;
import com.pedalbuildpipeline.pbp.event.outbox.repo.entity.OutboxEntry;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OutboxServiceTest {
  @InjectMocks public OutboxService outboxService;

  @Mock public OutboxRepository outboxRepository;

  @Mock public ObjectMapper objectMapper;

  @DisplayName("given a new event, when creating an entry, the record does translate and save")
  @Test
  public void createEntryDoesSaveRecord() throws JsonProcessingException {
    UUID userId = UUID.randomUUID();
    UserCreatedEvent userCreatedEvent = new UserCreatedEvent(userId);

    OutboxEntry outboxEntry = mock(OutboxEntry.class);
    String serializedEntry = "{}";

    when(objectMapper.writeValueAsString(userCreatedEvent)).thenReturn(serializedEntry);

    ArgumentCaptor<OutboxEntry> outboxEntryArgumentCaptor =
        ArgumentCaptor.forClass(OutboxEntry.class);
    when(outboxRepository.save(outboxEntryArgumentCaptor.capture())).thenReturn(outboxEntry);

    OutboxEntry savedOutboxEntry = outboxService.createEntry(userCreatedEvent);

    assertThat(savedOutboxEntry).isEqualTo(outboxEntry);

    assertThat(outboxEntryArgumentCaptor.getAllValues().size()).isEqualTo(1);
    OutboxEntry translatedEntry = outboxEntryArgumentCaptor.getValue();
    assertThat(translatedEntry.getAggregate()).isEqualTo(AggregateType.USER);
    assertThat(translatedEntry.getAggregateId()).isEqualTo(userId.toString());
    assertThat(translatedEntry.getEventType()).isEqualTo(EventType.USER_CREATED.name());
    assertThat(translatedEntry.getPayload()).isEqualTo(serializedEntry);
  }

  @DisplayName(
      "given a new event, when serialization fails when creating an entry, an exception is thrown")
  @Test
  public void createEntryDoesThrowOnSerializationFailure() throws JsonProcessingException {
    UUID userId = UUID.randomUUID();
    UserCreatedEvent userCreatedEvent = new UserCreatedEvent(userId);

    JsonProcessingException exception = mock(JsonProcessingException.class);

    when(objectMapper.writeValueAsString(userCreatedEvent)).thenThrow(exception);

    assertThrows(EventStorageException.class, () -> outboxService.createEntry(userCreatedEvent));
  }
}
