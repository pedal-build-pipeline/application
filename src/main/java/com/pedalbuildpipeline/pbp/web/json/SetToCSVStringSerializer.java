package com.pedalbuildpipeline.pbp.web.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

public class SetToCSVStringSerializer extends JsonSerializer<Collection<?>> {
  @Override
  public void serialize(Collection<?> value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    gen.writeString(value.stream().map(Object::toString).collect(Collectors.joining(",")));
  }
}
