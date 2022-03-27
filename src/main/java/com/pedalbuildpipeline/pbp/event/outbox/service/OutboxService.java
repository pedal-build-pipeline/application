package com.pedalbuildpipeline.pbp.event.outbox.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedalbuildpipeline.pbp.event.model.BaseEvent;
import com.pedalbuildpipeline.pbp.event.outbox.exception.EventStorageException;
import com.pedalbuildpipeline.pbp.event.outbox.repo.OutboxRepository;
import com.pedalbuildpipeline.pbp.event.outbox.repo.entity.OutboxEntry;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional(Transactional.TxType.MANDATORY)
@RequiredArgsConstructor
public class OutboxService {
  private final OutboxRepository outboxRepository;

  private final ObjectMapper objectMapper;

  public OutboxEntry createEntry(BaseEvent event) {
    try {
      return outboxRepository.save(
          OutboxEntry.builder()
              .eventType(event.getEventType().name())
              .aggregateId(event.getAggregateId())
              .aggregate(event.getAggregateType())
              .payload(objectMapper.writeValueAsString(event))
              .build());
    } catch (JsonProcessingException e) {
      throw new EventStorageException("Unable to serialize event for persistence", e);
    }
  }
}
