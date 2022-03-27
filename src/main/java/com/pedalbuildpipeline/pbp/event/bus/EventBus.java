package com.pedalbuildpipeline.pbp.event.bus;

public interface EventBus {
  void subscribe(EventListener<?> listener);

  void publish(Object object);
}
