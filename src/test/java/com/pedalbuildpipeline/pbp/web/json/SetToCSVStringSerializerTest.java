package com.pedalbuildpipeline.pbp.web.json;

import com.fasterxml.jackson.core.JsonGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SetToCSVStringSerializerTest {
  @DisplayName(
      "given a collection with multiple entries, when serializing, the collection is serialized as a comma separated value")
  @Test
  public void multipleEntries() throws IOException {
    JsonGenerator gen = mock(JsonGenerator.class);

    SetToCSVStringSerializer serializer = new SetToCSVStringSerializer();

    serializer.serialize(List.of(",a,", "b", "c"), gen, null);

    verify(gen).writeString("\\,a\\,,b,c");
  }

  @DisplayName(
      "given a collection with a single entry, when serializing, the collection is serialized as a single entry")
  @Test
  public void singleEntry() throws IOException {
    JsonGenerator gen = mock(JsonGenerator.class);

    SetToCSVStringSerializer serializer = new SetToCSVStringSerializer();

    serializer.serialize(List.of("a"), gen, null);

    verify(gen).writeString("a");
  }
}
