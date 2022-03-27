package com.pedalbuildpipeline.pbp.notification.email.mailpace.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pedalbuildpipeline.pbp.web.json.InputStreamToBase64StringSerializer;
import com.pedalbuildpipeline.pbp.web.json.SetToCSVStringSerializer;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Builder
@Getter
@Jacksonized
public class SendEmailRequest {
  private String from;

  @JsonSerialize(using = SetToCSVStringSerializer.class)
  private Set<String> to;

  private String htmlbody;
  private String textbody;

  @JsonSerialize(using = SetToCSVStringSerializer.class)
  private Set<String> cc;

  @JsonSerialize(using = SetToCSVStringSerializer.class, as = String.class)
  private Set<String> bcc;

  private String subject;

  @JsonSerialize(using = SetToCSVStringSerializer.class, as = String.class)
  private String replyto;

  @JsonSerialize(using = SetToCSVStringSerializer.class, as = String.class)
  private Set<String> list_unsubscribe;

  @JsonSerialize(
      contentUsing = InputStreamToBase64StringSerializer.class,
      as = List.class,
      contentAs = String.class)
  private List<InputStream> attachments;

  private Set<String> tags;
}
