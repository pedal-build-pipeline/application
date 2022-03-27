package com.pedalbuildpipeline.pbp.security.jwt;

import com.pedalbuildpipeline.pbp.security.jwt.annotation.Jwt;
import io.jsonwebtoken.*;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import java.time.Clock;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/*
 * Heavily inspired by JHipster JWTFilter (https://github.com/jhipster)
 */
@Service
@Slf4j
public class JwtTokenService {
  private static final String AUTHORITIES_KEY = "authorities";

  private final SecretKey key;
  private final JwtParser jwtParser;
  private final long tokenValidMinutes;
  private final Clock clock;
  private final JacksonSerializer<Map<String, ?>> jwtJacksonSerializer;
  private final UserDetailsService userDetailsService;

  public JwtTokenService(
      @Jwt SecretKey jwtSigningKey,
      JwtParser jwtParser,
      JwtConfigurationProperties jwtConfigurationProperties,
      Clock clock,
      JacksonSerializer<Map<String, ?>> jwtJacksonSerializer,
      UserDetailsService userDetailsService) {
    this.key = jwtSigningKey;
    this.jwtParser = jwtParser;
    this.tokenValidMinutes = jwtConfigurationProperties.getTokenValidMinutes();
    this.clock = clock;
    this.jwtJacksonSerializer = jwtJacksonSerializer;
    this.userDetailsService = userDetailsService;
  }

  public String createJwt(Authentication authentication) {
    return Jwts.builder()
        .setSubject(authentication.getName())
        .claim(
            AUTHORITIES_KEY,
            authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(",")))
        .signWith(key, SignatureAlgorithm.HS512)
        .setExpiration(Date.from(clock.instant().plus(tokenValidMinutes, ChronoUnit.MINUTES)))
        .serializeToJsonWith(jwtJacksonSerializer)
        .compact();
  }

  public Authentication toAuthentication(String token) {
    Claims claims = jwtParser.parseClaimsJws(token).getBody();

    Set<? extends GrantedAuthority> authorities =
        Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
            .filter(auth -> !auth.trim().isEmpty())
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toSet());

    UserDetails user = userDetailsService.loadUserByUsername(claims.getSubject());

    return new JwtAuthentication(user, token, authorities);
  }

  public boolean isValid(String token) {
    try {
      jwtParser.parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      log.debug("Received invalid JWT");
    }
    return false;
  }
}
