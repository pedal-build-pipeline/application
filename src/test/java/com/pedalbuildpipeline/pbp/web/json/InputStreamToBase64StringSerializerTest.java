package com.pedalbuildpipeline.pbp.web.json;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InputStreamToBase64StringSerializerTest {
  @DisplayName(
      "given an input stream and generator, when serializing, then the base64 encoded version of the input stream is written")
  @Test
  public void doesSerialize() throws IOException {
    JsonGenerator jsonGenerator = mock(JsonGenerator.class);

    InputStreamToBase64StringSerializer serializer = new InputStreamToBase64StringSerializer();

    InputStream inputStream =
        new ByteArrayInputStream("hello world".getBytes(StandardCharsets.UTF_8));

    serializer.serialize(inputStream, jsonGenerator, null);

    ArgumentCaptor<String> encodedCaptor = ArgumentCaptor.forClass(String.class);
    verify(jsonGenerator).writeString(encodedCaptor.capture());

    assertThat(new String(Base64.getDecoder().decode(encodedCaptor.getValue())))
        .isEqualTo("hello world");
  }
}
