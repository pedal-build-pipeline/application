package com.pedalbuildpipeline.pbp;

import static org.assertj.core.api.Assertions.assertThat;

import com.pedalbuildpipeline.pbp.event.outbox.repo.OutboxRepository;
import com.pedalbuildpipeline.pbp.event.outbox.repo.entity.OutboxEntry;
import java.util.List;
import java.util.stream.StreamSupport;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;

public class OutboxVerifyingTestBase extends ComponentTestBase {
  @Autowired protected OutboxRepository outboxRepository;

  @BeforeEach
  @AfterEach
  public void clearOutbox() {
    outboxRepository.deleteAll();
  }

  protected void verifyOutboxEntries(List<OutboxEntry> expectedOutboxEntries) {
    List<OutboxEntry> foundOutboxEntries =
        StreamSupport.stream(outboxRepository.findAll().spliterator(), false).toList();

    assertThat(expectedOutboxEntries.size()).isEqualTo(foundOutboxEntries.size());

    expectedOutboxEntries.forEach(
        expectedEntry -> {
          assertThat(
                  foundOutboxEntries.stream()
                      .filter(
                          foundEntry -> {
                            try {
                              return (expectedEntry.getEventType() == null
                                      || foundEntry
                                          .getEventType()
                                          .equals(expectedEntry.getEventType()))
                                  && (expectedEntry.getAggregate() == null
                                      || foundEntry
                                          .getAggregate()
                                          .equals(expectedEntry.getAggregate()))
                                  && (expectedEntry.getAggregateId() == null
                                      || foundEntry
                                          .getAggregateId()
                                          .equals(expectedEntry.getAggregateId()))
                                  && (expectedEntry.getPayload() == null
                                      || JSONCompare.compareJSON(
                                              expectedEntry.getPayload(),
                                              foundEntry.getPayload(),
                                              JSONCompareMode.STRICT_ORDER)
                                          .passed());
                            } catch (JSONException e) {
                              throw new RuntimeException("Invalid JSON encountered");
                            }
                          })
                      .findFirst())
              .isNotEmpty();
        });
  }
}
