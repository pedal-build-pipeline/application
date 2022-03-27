package com.pedalbuildpipeline.pbp.event.bus.simple;

import com.pedalbuildpipeline.pbp.event.bus.EventBus;
import com.pedalbuildpipeline.pbp.event.bus.EventListener;
import com.pedalbuildpipeline.pbp.event.bus.exception.InvalidSubscriberException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleInMemoryThrowingEventBus implements EventBus {
  private final EventListenerRegistry eventListenerRegistry = new EventListenerRegistry();

  @Override
  public void subscribe(EventListener<?> listener) {
    eventListenerRegistry.add(listener);
  }

  @Override
  public void publish(Object event) {
    Class<?> specificClass = event.getClass();
    do {
      // Future performance improvement -- it feels like we could cache this looping or do it up front
      // be examining all subscribed-to classes.
      for (EventConsumer eventConsumer : eventListenerRegistry.get(specificClass)) {
        eventConsumer.consume(event);
      }

      for (Class<?> implementedInterface : specificClass.getInterfaces()) {
        for (EventConsumer eventConsumer : eventListenerRegistry.get(implementedInterface)) {
          eventConsumer.consume(event);
        }
      }

      specificClass = specificClass.getSuperclass();
    } while (specificClass != null);
  }

  private static final class EventListenerRegistry {
    private final ConcurrentHashMap<Class<?>, List<EventConsumer>> subscriberInvokersByEventClass =
        new ConcurrentHashMap<>();

    public void add(EventListener<?> eventListener) {
      Method consumeMethod =
          Arrays.stream(eventListener.getClass().getMethods())
              .filter(
                  method -> method.getName().equals("consume") && method.getParameterCount() == 1)
              .findFirst()
              .orElseThrow(
                  () ->
                      new InvalidSubscriberException(
                          "Subscriber of class "
                              + eventListener.getClass()
                              + " requires one method named consume that accepts one parameter, but none found."));

      Class<?> specificClass = consumeMethod.getParameterTypes()[0];

      EventConsumer eventConsumer = new EventConsumer(eventListener, consumeMethod);

      subscriberInvokersByEventClass
              .computeIfAbsent(specificClass, (k) -> new ArrayList<>())
              .add(eventConsumer);
    }

    public List<EventConsumer> get(Class<?> eventClass) {
      return Optional.ofNullable(subscriberInvokersByEventClass.get(eventClass))
          .orElseGet(ArrayList::new);
    }
  }

  private record EventConsumer(EventListener<?> listener, Method method) {
    public void consume(Object event) {
      try {
        method.invoke(listener, event);
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new InvalidSubscriberException(
            "Consume called on subscriber with class "
                + listener.getClass()
                + " "
                + "with event of type "
                + event.getClass()
                + " failed",
            e);
      }
    }
  }
}
