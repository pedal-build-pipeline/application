package com.pedalbuildpipeline.pbp.notification.email.repo;

import com.pedalbuildpipeline.pbp.notification.email.entity.SentEmailNotificationRecord;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface SentEmailNotificationRecordRepository
    extends CrudRepository<SentEmailNotificationRecord, UUID> {}
