package com.pedalbuildpipeline.pbp.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import io.jsonwebtoken.*;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import io.jsonwebtoken.security.Keys;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

class JwtTokenServiceTest {
  private JwtTokenService jwtTokenService;

  private SecretKey secretKey;
  private JwtParser jwtParser;
  private Clock clock;
  private UserDetailsService userDetailsService;

  @BeforeEach
  public void setup() {
    secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    jwtParser = mock(JwtParser.class);
    clock = mock(Clock.class);
    JacksonSerializer<Map<String, ?>> jwtJacksonSerializer = new JacksonSerializer<>();
    userDetailsService = mock(UserDetailsService.class);

    JwtConfigurationProperties jwtConfigurationProperties = new JwtConfigurationProperties();
    jwtConfigurationProperties.setTokenValidMinutes(30L);

    jwtTokenService =
        new JwtTokenService(
            secretKey,
            jwtParser,
            jwtConfigurationProperties,
            clock,
            jwtJacksonSerializer,
            userDetailsService);
  }

  @DisplayName("given authentication details, when creating a jwt, the proper jwt is returned")
  @Test
  public void createJwtFromAuthentication() {
    io.jsonwebtoken.Clock jwtClock = mock(io.jsonwebtoken.Clock.class);
    JwtParser localParser =
        Jwts.parserBuilder().setSigningKey(secretKey).setClock(jwtClock).build();

    when(clock.instant()).thenReturn(Instant.ofEpochSecond(1649619264));
    when(jwtClock.now()).thenReturn(Date.from(Instant.ofEpochSecond(1649619264)));

    String jwt =
        jwtTokenService.createJwt(
            new UsernamePasswordAuthenticationToken(
                "test",
                "pass",
                List.of(new SimpleGrantedAuthority("AUTH1"), new SimpleGrantedAuthority("AUTH2"))));

    Claims claims = localParser.parseClaimsJws(jwt).getBody();

    assertThat(claims.getExpiration().getTime()).isEqualTo(1649621064000L);
    assertThat(claims.getSubject()).isEqualTo("test");
    assertThat(claims.get("authorities")).isEqualTo("AUTH1,AUTH2");
  }

  @DisplayName(
      "given a token, when converting to authentication, then the proper authentication is returned")
  @Test
  public void tokenToAuthentication() {
    UserDetails userDetails = mock(UserDetails.class);
    Jws<Claims> jws = mock(Jws.class);
    Claims claims = mock(Claims.class);
    String jwt = "abcd1234";

    when(jwtParser.parseClaimsJws(jwt)).thenReturn(jws);
    when(jws.getBody()).thenReturn(claims);
    when(claims.get("authorities")).thenReturn("AUTH1,AUTH2");
    when(claims.getSubject()).thenReturn("test");
    when(userDetailsService.loadUserByUsername("test")).thenReturn(userDetails);

    Authentication authentication = jwtTokenService.toAuthentication(jwt);

    assertThat(authentication).isInstanceOf(JwtAuthentication.class);
    assertThat(authentication.getPrincipal()).isEqualTo(userDetails);
    assertThat(authentication.getCredentials()).isEqualTo(jwt);
    assertThat(
            authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet()))
        .isEqualTo(Set.of("AUTH1", "AUTH2"));

    verify(jwtParser).parseClaimsJws(jwt);
  }

  @DisplayName("given an invalid token, when checking validity, then false is returned")
  @ParameterizedTest(name = "{index} ==> exception of type {0}")
  @MethodSource("validationExceptions")
  public void isValidWithException(Exception thrown) {
    String token = "token";

    when(jwtParser.parseClaimsJws(token)).thenThrow(thrown);

    boolean valid = jwtTokenService.isValid(token);

    assertThat(valid).isFalse();

    verify(jwtParser).parseClaimsJws(token);
  }

  public static Stream<Arguments> validationExceptions() {
    return Stream.of(
        Arguments.of(Named.of("JwtException", new MalformedJwtException("Invalid jwt"))),
        Arguments.of(
            Named.of("IllegalArgumentException", new IllegalArgumentException("Illegal jwt arg"))));
  }
}
