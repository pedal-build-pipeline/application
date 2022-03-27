package com.pedalbuildpipeline.pbp.event.outbox.config;

import com.pedalbuildpipeline.pbp.event.outbox.annotation.OutboxProcessing;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class OutboxProcessingConfiguration {
  @Bean
  @OutboxProcessing
  public AsyncTaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setCorePoolSize(1);
    taskExecutor.setMaxPoolSize(1);
    return taskExecutor;
  }
}
