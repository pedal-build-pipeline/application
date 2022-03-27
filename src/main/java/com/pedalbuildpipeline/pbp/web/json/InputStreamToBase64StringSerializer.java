package com.pedalbuildpipeline.pbp.web.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class InputStreamToBase64StringSerializer extends JsonSerializer<InputStream> {
  @Override
  public void serialize(InputStream value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    gen.writeString(Base64.getEncoder().encodeToString(value.readAllBytes()));
  }
}
