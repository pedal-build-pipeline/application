package com.pedalbuildpipeline.pbp;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.bouncycastle.util.encoders.Base64;
import org.junit.jupiter.api.Test;

public class KeyGeneratorTest {
  @Test
  public void generateKey() {
    System.out.println(Base64.toBase64String(Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded()));
  }
}
