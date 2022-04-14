package com.pedalbuildpipeline.pbp.user.controller;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.pedalbuildpipeline.pbp.OutboxVerifyingTestBase;
import com.pedalbuildpipeline.pbp.event.AggregateType;
import com.pedalbuildpipeline.pbp.event.EventType;
import com.pedalbuildpipeline.pbp.event.outbox.repo.entity.OutboxEntry;
import com.pedalbuildpipeline.pbp.user.dto.AuthenticationResponse;
import com.pedalbuildpipeline.pbp.user.dto.UserDto;
import com.pedalbuildpipeline.pbp.user.dto.UserRegistrationDto;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;

class UserControllerIntegrationTest extends OutboxVerifyingTestBase {
  @DisplayName(
      "given a valid user registration, when the user is registered, then a user is created, the user location is returned, and an outbox entry is created")
  @Test
  @Order(1)
  public void newUserRegistration() throws Exception {
    UserRegistrationDto userRegistrationDto =
        new UserRegistrationDto("user-a", "test@test.com", "abcd1234");

    MockHttpServletResponse response =
        mockMvc
            .perform(
                post("/api/users/registration")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json(userRegistrationDto)))
            .andReturn()
            .getResponse();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    assertThat(response.getHeader(HttpHeaders.LOCATION)).matches(".*/api/users/[^/]+");

    Matcher matcher =
        Pattern.compile(".*/api/users/(.*)").matcher(response.getHeader(HttpHeaders.LOCATION));
    assertThat(matcher.matches()).isTrue();
    String userId = matcher.group(1);

    verifyOutboxEntries(
        List.of(
            OutboxEntry.builder()
                .payload("{\"id\":\"" + userId + "\"}")
                .aggregate(AggregateType.USER)
                .eventType(EventType.USER_CREATED.name())
                .aggregateId(userId)
                .build()));
  }

  @DisplayName(
      "given a user with correct username and password, when the user authenticates, then the id token is returned")
  @Test
  @Order(2)
  public void newUserCanAuthenticate() throws Exception {
    MockHttpServletResponse response =
        mockMvc
            .perform(
                post("/api/users/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\":\"user-a\",\"password\":\"abcd1234\"}"))
            .andReturn()
            .getResponse();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    assertThatJson(response.getContentAsString()).node("idToken").isString();
  }

  @DisplayName(
      "given an invalid password, when the user attempts to authenticate, then a 401 is returned")
  @Test
  @Order(3)
  public void invalidPassword401() throws Exception {
    MockHttpServletResponse response =
        mockMvc
            .perform(
                post("/api/users/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\":\"user-a\",\"password\":\"abcd1235\"}"))
            .andReturn()
            .getResponse();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
  }

  @DisplayName(
      "given a user token, when the self endpoint is called, then the user details corresponding to the token are returned")
  @Test
  @Order(4)
  public void getSelfUser() throws Exception {
    MockHttpServletResponse response =
        mockMvc
            .perform(
                get("/api/users/self").header(HttpHeaders.AUTHORIZATION, "Bearer " + getToken()))
            .andReturn()
            .getResponse();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());

    UserDto responseUser = objectMapper.readValue(response.getContentAsString(), UserDto.class);
    assertThat(responseUser.getUsername()).isEqualTo("user-a");
    assertThat(responseUser.getEmail()).isEqualTo("test@test.com");
    assertThat(responseUser.getId()).isNotNull();
  }

  @DisplayName(
      "given no token, when the self endpoint is called, then an unauthorized error is returned")
  @Test
  @Order(5)
  public void getSelfUserNoToken() throws Exception {
    MockHttpServletResponse response =
        mockMvc.perform(get("/api/users/self")).andReturn().getResponse();

    assertThat(response.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
  }

  protected String getToken() throws Exception {
    return getToken("user-a", "abcd1234");
  }

  protected String getToken(String username, String password) throws Exception {
    MockHttpServletResponse response =
        mockMvc
            .perform(
                post("/api/users/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
            .andReturn()
            .getResponse();

    return objectMapper
        .readValue(response.getContentAsString(), AuthenticationResponse.class)
        .getIdToken();
  }
}
