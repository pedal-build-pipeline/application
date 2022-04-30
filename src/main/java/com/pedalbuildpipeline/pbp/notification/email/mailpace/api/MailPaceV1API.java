package com.pedalbuildpipeline.pbp.notification.email.mailpace.api;

import com.pedalbuildpipeline.pbp.notification.email.mailpace.config.FeignConfiguration;
import com.pedalbuildpipeline.pbp.notification.email.mailpace.model.SendEmailRequest;
import com.pedalbuildpipeline.pbp.notification.email.mailpace.model.SendEmailResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(
    value = "mailpace",
    url = "${notifications.email.api-url}",
    configuration = FeignConfiguration.class)
@ConditionalOnProperty(value = "notifications.email.provider", havingValue = "mailpace")
public interface MailPaceV1API {
  @RequestMapping(
      method = RequestMethod.POST,
      value = "/v1/send",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  SendEmailResponse sendEmail(SendEmailRequest request);
}
