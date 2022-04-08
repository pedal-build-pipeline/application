package com.pedalbuildpipeline.pbp.notification.email.service;

import com.pedalbuildpipeline.pbp.notification.email.entity.SentEmailNotificationRecord;
import com.pedalbuildpipeline.pbp.notification.email.repo.SentEmailNotificationRecordRepository;
import com.pedalbuildpipeline.pbp.notification.model.NotificationDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SentEmailNotificationServiceTest {
  @InjectMocks SentEmailNotificationService sentEmailNotificationService;

  @Mock SentEmailNotificationRecordRepository sentEmailNotificationRecordRepository;

  @DisplayName(
      "given a user and notification details, when saving notification details, the expected notification record is saved")
  @Test
  public void saveNotificationDetails() {
    SentEmailNotificationRecord sentEmailNotificationRecord =
        mock(SentEmailNotificationRecord.class);

    UUID userId = UUID.randomUUID();
    NotificationDetails notificationDetails =
        new NotificationDetails("mailpace", "12345", "QUEUED", Map.of("some", "metadata"));

    when(sentEmailNotificationRecordRepository.save(
            argThat(
                (record) ->
                    "12345".equals(record.getProviderId())
                        && "mailpace".equals(record.getProvider())
                        && userId.equals(record.getUserId())
                        && "QUEUED".equals(record.getStatus())
                        && Map.of("some", "metadata").equals(record.getMetadata()))))
        .thenReturn(sentEmailNotificationRecord);

    assertThat(sentEmailNotificationService.saveNotificationDetails(userId, notificationDetails))
        .isEqualTo(sentEmailNotificationRecord);

    verify(sentEmailNotificationRecordRepository).save(argThat(
            (record) ->
                    "12345".equals(record.getProviderId())
                            && "mailpace".equals(record.getProvider())
                            && userId.equals(record.getUserId())
                            && "QUEUED".equals(record.getStatus())
                            && Map.of("some", "metadata").equals(record.getMetadata())));
  }
}
