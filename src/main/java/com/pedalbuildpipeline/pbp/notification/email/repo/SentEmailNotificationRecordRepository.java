package com.pedalbuildpipeline.pbp.notification.email.repo;

import com.pedalbuildpipeline.pbp.notification.email.entity.SentEmailNotificationRecord;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface SentEmailNotificationRecordRepository
    extends CrudRepository<SentEmailNotificationRecord, UUID> {}
