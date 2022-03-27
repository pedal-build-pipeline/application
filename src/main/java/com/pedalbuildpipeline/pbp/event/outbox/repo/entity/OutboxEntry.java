package com.pedalbuildpipeline.pbp.event.outbox.repo.entity;

import com.pedalbuildpipeline.pbp.event.AggregateType;
import java.util.UUID;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "outbox")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEntry {
  @Id
  @Column(name = "id", nullable = false, updatable = false)
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
  private UUID id;

  @Enumerated(EnumType.STRING)
  @Column(name = "aggregate", nullable = false, updatable = false)
  private AggregateType aggregate;

  @Column(name = "aggregate_id", nullable = false, updatable = false)
  private String aggregateId;

  @Column(name = "event_type", nullable = false, updatable = false)
  private String eventType;

  @Column(name = "payload", nullable = false, updatable = false)
  private String payload;

  @Column(name = "insert_order", nullable = false, updatable = false)
  @Generated(GenerationTime.INSERT)
  private Integer insertOrder;
}
