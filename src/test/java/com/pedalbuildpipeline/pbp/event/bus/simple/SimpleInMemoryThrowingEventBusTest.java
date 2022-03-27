package com.pedalbuildpipeline.pbp.event.bus.simple;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

import com.pedalbuildpipeline.pbp.event.bus.EventListener;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

@SuppressWarnings("unchecked")
class SimpleInMemoryThrowingEventBusTest {
  SimpleInMemoryThrowingEventBus bus;

  @BeforeEach
  void setup() {
    bus = new SimpleInMemoryThrowingEventBus();
  }

  @Test
  void registerDirectSubscriber() {
    Consumer<MockEvent> mockConsumer = (Consumer<MockEvent>) mock(Consumer.class);
    EventListener<MockEvent> listener =
        // Don't reduce to lambda -- we lose the type on the arg
        new EventListener<>() {
          @Override
          public void consume(MockEvent event) {
            mockConsumer.accept(event);
          }
        };

    bus.subscribe(listener);

    bus.publish(new MockEvent("two", "one", "end"));

    ArgumentCaptor<MockEvent> mockEventArgumentCaptor = ArgumentCaptor.forClass(MockEvent.class);
    verify(mockConsumer).accept(mockEventArgumentCaptor.capture());
    MockEvent capturedEvent = mockEventArgumentCaptor.getValue();
    assertThat(capturedEvent.getMockEventField()).isEqualTo("end");
    assertThat(capturedEvent.getMockEventSuperOneField()).isEqualTo("one");
    assertThat(capturedEvent.getMockEventSuperTwoField()).isEqualTo("two");
    reset(mockConsumer);

    bus.publish(new MockEventSuperTwo("twoOnly"));

    verify(mockConsumer, times(0)).accept(any());
  }

  @Test
  void registerSuperSubscriber() {
    Consumer<MockEventSuperOne> mockConsumer = (Consumer<MockEventSuperOne>) mock(Consumer.class);
    EventListener<MockEventSuperOne> listener =
        // Don't reduce to lambda -- we lose the type on the arg
        new EventListener<MockEventSuperOne>() {
          @Override
          public void consume(MockEventSuperOne event) {
            mockConsumer.accept(event);
          }
        };

    bus.subscribe(listener);

    bus.publish(new MockEventSuperOne("two", "one"));

    ArgumentCaptor<MockEventSuperOne> mockEventArgumentCaptor =
        ArgumentCaptor.forClass(MockEventSuperOne.class);
    verify(mockConsumer).accept(mockEventArgumentCaptor.capture());
    MockEventSuperOne capturedEvent = mockEventArgumentCaptor.getValue();
    assertThat(capturedEvent.getMockEventSuperOneField()).isEqualTo("one");
    assertThat(capturedEvent.getMockEventSuperTwoField()).isEqualTo("two");
    reset(mockConsumer);

    bus.publish(new MockEventSuperTwo("twoOnly"));

    verify(mockConsumer, times(0)).accept(any());
  }

  @Test
  void registerInterfaceSubscriber() {
    Consumer<SomeInterface> mockConsumer = (Consumer<SomeInterface>) mock(Consumer.class);
    EventListener<SomeInterface> listener =
        // Don't reduce to lambda -- we lose the type on the arg
        new EventListener<SomeInterface>() {
          @Override
          public void consume(SomeInterface event) {
            mockConsumer.accept(event);
          }
        };

    bus.subscribe(listener);

    bus.publish(new MockEventSuperTwo("two"));

    ArgumentCaptor<SomeInterface> mockEventArgumentCaptor =
        ArgumentCaptor.forClass(SomeInterface.class);
    verify(mockConsumer).accept(mockEventArgumentCaptor.capture());
    SomeInterface capturedEvent = mockEventArgumentCaptor.getValue();
    assertThat(capturedEvent).isInstanceOf(MockEventSuperTwo.class);
    assertThat(((MockEventSuperTwo) capturedEvent).getMockEventSuperTwoField()).isEqualTo("two");
  }

  private static class MockEvent extends MockEventSuperOne {
    private final String mockEventField;

    public MockEvent(
        String mockEventSuperTwoField, String mockEventSuperOneField, String mockEventField) {
      super(mockEventSuperTwoField, mockEventSuperOneField);
      this.mockEventField = mockEventField;
    }

    public String getMockEventField() {
      return mockEventField;
    }
  }

  private static class MockEventSuperOne extends MockEventSuperTwo {
    private final String mockEventSuperOneField;

    public MockEventSuperOne(String mockEventSuperTwoField, String mockEventSuperOneField) {
      super(mockEventSuperTwoField);
      this.mockEventSuperOneField = mockEventSuperOneField;
    }

    public String getMockEventSuperOneField() {
      return mockEventSuperOneField;
    }
  }

  private static class MockEventSuperTwo implements SomeInterface {
    private final String mockEventSuperTwoField;

    public MockEventSuperTwo(String mockEventSuperTwoField) {
      this.mockEventSuperTwoField = mockEventSuperTwoField;
    }

    public String getMockEventSuperTwoField() {
      return mockEventSuperTwoField;
    }
  }

  private interface SomeInterface {}
}
