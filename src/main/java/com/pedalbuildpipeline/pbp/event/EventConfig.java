package com.pedalbuildpipeline.pbp.event;

import com.pedalbuildpipeline.pbp.event.bus.EventBus;
import com.pedalbuildpipeline.pbp.event.bus.EventListener;
import com.pedalbuildpipeline.pbp.event.bus.simple.SimpleInMemoryThrowingEventBus;
import com.pedalbuildpipeline.pbp.event.exception.InvalidEventConfigurationException;
import com.pedalbuildpipeline.pbp.event.model.user.UserCreatedEvent;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventConfig {
  @Bean
  public EventTypeRegistry eventTypeRegistry() {
    Map<EventType, Class<?>> eventClassesByType = new EnumMap<>(EventType.class);
    eventClassesByType.put(EventType.USER_CREATED, UserCreatedEvent.class);

    Set<EventType> unmappedEventTypes =
        Arrays.stream(EventType.values())
            .filter(eventType -> !eventClassesByType.containsKey(eventType))
            .collect(Collectors.toSet());

    if (!unmappedEventTypes.isEmpty()) {
      throw new InvalidEventConfigurationException(
          "Events of types ["
              + unmappedEventTypes.stream().map(EventType::name).collect(Collectors.joining(", "))
              + "] contain no class mapping in the event registry");
    }

    return new EventTypeRegistry(eventClassesByType);
  }

  @Bean
  EventBus eventBus(List<EventListener<?>> registeredEventListeners) {
    EventBus eventBus = new SimpleInMemoryThrowingEventBus();

    registeredEventListeners.forEach(eventBus::subscribe);

    return eventBus;
  }
}
