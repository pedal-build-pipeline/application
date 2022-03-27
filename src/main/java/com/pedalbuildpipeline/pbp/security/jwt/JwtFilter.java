package com.pedalbuildpipeline.pbp.security.jwt;

import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/*
 * Heavily inspired by JHipster JWTFilter (https://github.com/jhipster)
 *
 * Kind of wonder if this should really be an AbstractAuthenticationProcessingFilter, but I
 * guess there's no way to set up the authentication without parsing the JWT, so you immediately
 * know if it's valid or not and you'd be doubling the work?
 */
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
  private static final String BEARER_TOKEN_PREFIX = "Bearer ";

  private final JwtTokenService jwtTokenService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    Optional<String> jwt = getToken(request);

    if (jwt.isPresent() && jwtTokenService.isValid(jwt.get())) {
      Authentication authentication = jwtTokenService.toAuthentication(jwt.get());
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    filterChain.doFilter(request, response);
  }

  protected Optional<String> getToken(HttpServletRequest request) {
    Optional<String> token = Optional.empty();

    String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (StringUtils.hasText(authorizationHeader)
        && authorizationHeader.startsWith(BEARER_TOKEN_PREFIX)) {
      String tokenPart = authorizationHeader.substring(BEARER_TOKEN_PREFIX.length());
      if (StringUtils.hasText(tokenPart)) {
        token = Optional.of(tokenPart);
      }
    }

    return token;
  }
}
