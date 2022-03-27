package com.pedalbuildpipeline.pbp.event;

import lombok.RequiredArgsConstructor;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class EventTypeRegistry {
  private Map<EventType, Class<?>> eventClassByType = new EnumMap<>(EventType.class);

  public EventTypeRegistry(Map<EventType, Class<?>> eventClassByType) {
    this.eventClassByType = eventClassByType;
  }

  public Optional<Class<?>> getEventClassForType(String eventType) {
    return Optional.ofNullable(eventClassByType.get(EventType.valueOf(eventType)));
  }
}
