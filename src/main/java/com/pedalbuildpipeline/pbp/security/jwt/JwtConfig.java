package com.pedalbuildpipeline.pbp.security.jwt;

import com.pedalbuildpipeline.pbp.security.jwt.annotation.Jwt;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import io.jsonwebtoken.security.Keys;
import java.util.Map;
import javax.crypto.SecretKey;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
  @Bean
  public JacksonSerializer<Map<String, ?>> jwtJacksonSerializer() {
    return new JacksonSerializer<>();
  }

  @Bean
  @ConfigurationProperties("security.jwt")
  public JwtConfigurationProperties jwtConfigurationProperties() {
    return new JwtConfigurationProperties();
  }

  @Bean
  @Jwt
  public SecretKey jwtSecretKey(JwtConfigurationProperties jwtConfigurationProperties) {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtConfigurationProperties.getBase64Secret()));
  }

  @Bean
  public JwtParser jwtParser(@Jwt SecretKey jwtSecretKey) {
    return Jwts.parserBuilder().setSigningKey(jwtSecretKey).build();
  }
}
