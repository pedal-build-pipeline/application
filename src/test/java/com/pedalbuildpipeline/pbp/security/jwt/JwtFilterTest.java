package com.pedalbuildpipeline.pbp.security.jwt;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {
  @InjectMocks private JwtFilter jwtFilter;

  @Mock private JwtTokenService jwtTokenService;

  @Mock private FilterChain filterChain;

  @BeforeEach
  public void beforeEach() {
    SecurityContextHolder.clearContext();
  }

  @AfterEach
  public void afterEach() {
    SecurityContextHolder.clearContext();
  }

  @DisplayName(
      "given a no authorization header, when the filter is applied, the authentication is not set")
  @Test
  public void noAuthorizationHeader() throws ServletException, IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

    jwtFilter.doFilterInternal(request, response, filterChain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

    verify(filterChain).doFilter(request, response);
    verifyNoInteractions(jwtTokenService);
  }

  @DisplayName(
      "given a non-bearer authorization header, when the filter is applied, the authentication is not set")
  @Test
  public void nonBearerAuthorizationHeader() throws ServletException, IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic AAdsfdsc=");

    jwtFilter.doFilterInternal(request, response, filterChain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

    verify(filterChain).doFilter(request, response);
    verifyNoInteractions(jwtTokenService);
  }

  @DisplayName(
      "given a bearer authorization header with an empty token, when the filter is applied, the authentication is not set")
  @Test
  public void emptyTokenBearerAuthorizationHeader() throws ServletException, IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("");

    jwtFilter.doFilterInternal(request, response, filterChain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

    verify(filterChain).doFilter(request, response);
    verifyNoInteractions(jwtTokenService);
  }

  @DisplayName(
      "given a populated bearer token that is invalid, when the filter is applied, then authentication is not set")
  @Test
  public void invalidBearerToken() throws ServletException, IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer abcd1234");
    when(jwtTokenService.isValid("abcd1234")).thenReturn(false);

    jwtFilter.doFilterInternal(request, response, filterChain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

    verify(filterChain).doFilter(request, response);
    verify(jwtTokenService).isValid("abcd1234");
  }

  @DisplayName(
      "given a populated and valid bearer token, when the filter is applied, then authentication is set")
  @Test
  public void validBearerToken() throws ServletException, IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    Authentication authentication = mock(Authentication.class);

    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer abcd1234");
    when(jwtTokenService.isValid("abcd1234")).thenReturn(true);
    when(jwtTokenService.toAuthentication("abcd1234")).thenReturn(authentication);

    jwtFilter.doFilterInternal(request, response, filterChain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(authentication);

    verify(filterChain).doFilter(request, response);
    verify(jwtTokenService).isValid("abcd1234");
    verify(jwtTokenService).toAuthentication("abcd1234");
  }
}
