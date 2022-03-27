package com.pedalbuildpipeline.pbp.security.jwt;

import java.util.Collection;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

@Getter
@ToString
public class JwtAuthentication extends AbstractAuthenticationToken {
  private final Object principal;
  private final Object credentials;

  public JwtAuthentication(
      Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
    this.principal = principal;
    this.credentials = credentials;
    setAuthenticated(true);
  }
}
