package com.pedalbuildpipeline.pbp.event.model.user;

import com.pedalbuildpipeline.pbp.event.AggregateType;
import com.pedalbuildpipeline.pbp.event.EventType;
import com.pedalbuildpipeline.pbp.event.model.Aggregate;
import com.pedalbuildpipeline.pbp.event.model.BaseEvent;

public record UserCreatedEvent(String id) implements BaseEvent {
  @Override
  public EventType getEventType() {
    return EventType.USER_CREATED;
  }

  @Override
  public AggregateType getAggregateType() {
    return AggregateType.USER;
  }

  @Override
  public String getAggregateId() {
    return Aggregate.USER.name();
  }
}
