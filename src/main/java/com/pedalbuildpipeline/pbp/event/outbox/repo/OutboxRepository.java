package com.pedalbuildpipeline.pbp.event.outbox.repo;

import com.pedalbuildpipeline.pbp.event.outbox.repo.entity.OutboxEntry;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxRepository extends CrudRepository<OutboxEntry, UUID> {}
