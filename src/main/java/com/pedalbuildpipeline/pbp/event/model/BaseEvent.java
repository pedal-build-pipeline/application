package com.pedalbuildpipeline.pbp.event.model;

import com.pedalbuildpipeline.pbp.event.AggregateType;
import com.pedalbuildpipeline.pbp.event.EventType;

public interface BaseEvent {
  EventType getEventType();

  AggregateType getAggregateType();

  String getAggregateId();
}
