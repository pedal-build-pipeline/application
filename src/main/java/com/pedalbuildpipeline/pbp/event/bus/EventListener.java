package com.pedalbuildpipeline.pbp.event.bus;

public interface EventListener<T> {
  void consume(T event);
}
