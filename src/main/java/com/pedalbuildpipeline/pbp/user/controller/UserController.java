package com.pedalbuildpipeline.pbp.user.controller;

import com.pedalbuildpipeline.pbp.ResourceType;
import com.pedalbuildpipeline.pbp.security.authorization.annotation.HasAdmin;
import com.pedalbuildpipeline.pbp.security.jwt.JwtTokenService;
import com.pedalbuildpipeline.pbp.user.dto.AuthenticationResponse;
import com.pedalbuildpipeline.pbp.user.dto.UserAuthenticationDto;
import com.pedalbuildpipeline.pbp.user.dto.UserDto;
import com.pedalbuildpipeline.pbp.user.dto.UserRegistrationDto;
import com.pedalbuildpipeline.pbp.user.dto.hateoas.UserModelAssembler;
import com.pedalbuildpipeline.pbp.web.exception.ResourceNotFoundException;
import com.pedalbuildpipeline.pbp.user.mapping.UserMapper;
import com.pedalbuildpipeline.pbp.user.repo.entity.User;
import com.pedalbuildpipeline.pbp.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/users")
@Validated
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;
  private final UserMapper userMapper;
  private final UserModelAssembler userRegistrationModelAssembler;
  private final AuthenticationManager authenticationManager;
  private final JwtTokenService jwtTokenService;

  @PostMapping("/registration")
  @Operation(description = "Register a new user of the system")
  @ApiResponses({
    @ApiResponse(description = "User is successfully registered", responseCode = "201"),
    @ApiResponse(description = "The request data is invalid", responseCode = "400"),
    @ApiResponse(description = "The username is already in use", responseCode = "409")
  })
  public ResponseEntity<Void> registerUser(
      @Valid @RequestBody UserRegistrationDto userRegistrationDto) {
    User createdUser = userService.registerUser(userMapper.toUser(userRegistrationDto));

    return ResponseEntity.created(
            linkTo(methodOn(UserController.class).getUser(createdUser.getId())).toUri())
        .build();
  }

  @PostMapping("/authenticate")
  @Operation(description = "Authenticate in as the given user, returning an access token")
  @ApiResponses({
    @ApiResponse(description = "Successful authentication", responseCode = "200"),
    @ApiResponse(description = "Invalid request", responseCode = "400"),
    @ApiResponse(description = "Invalid credentials", responseCode = "401")
  })
  public ResponseEntity<AuthenticationResponse> authenticate(
      @Valid @RequestBody UserAuthenticationDto userAuthenticationDto) {
    return ResponseEntity.ok(
        new AuthenticationResponse(
            jwtTokenService.createJwt(
                authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                        userAuthenticationDto.getUsername(),
                        userAuthenticationDto.getPassword())))));
  }

  @GetMapping("/self")
  @Operation(description = "Retrieve own user from token details")
  @ApiResponses({
    @ApiResponse(
        description = "Token was accessible and user details returned",
        responseCode = "200"),
    @ApiResponse(
        description = "The user details in the token were invalid or not available",
        responseCode = "400"),
    @ApiResponse(description = "Request did not contain proper authorization", responseCode = "401")
  })
  public ResponseEntity<UserDto> getSelf() {
    String username =
        ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .getUsername();
    return ResponseEntity.ok(
        userService
            .findByUsername(username)
            .map(userRegistrationModelAssembler::toModel)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(ResourceType.USER, username)));
  }

  @GetMapping("/{id}")
  @Operation(description = "Retrieve an existing user")
  @ApiResponses({
    @ApiResponse(description = "The user exists and data is returned", responseCode = "200"),
    @ApiResponse(description = "The user id was invalid or not provided", responseCode = "400"),
    @ApiResponse(description = "No user exists for the provided id", responseCode = "404")
  })
  public ResponseEntity<UserDto> getUser(@PathVariable("id") UUID id) {
    return ResponseEntity.ok(
        userService
            .findUser(id)
            .map(userRegistrationModelAssembler::toModel)
            .orElseThrow(
                () -> new ResourceNotFoundException(ResourceType.USER, id.toString())));
  }

  @GetMapping
  @Operation(description = "Retrieve a paginated list of users")
  @ApiResponses({
    @ApiResponse(description = "Insufficient permissions", responseCode = "401"),
    @ApiResponse(description = "Invalid search parameters", responseCode = "400"),
    @ApiResponse(description = "User search results are returned", responseCode = "200")
  })
  @HasAdmin
  public ResponseEntity<PagedModel<UserDto>> getUsers(
      @RequestParam(value = "username", required = false) Optional<String> username,
      @PageableDefault(size = 50, sort = "username") Pageable pageable,
      PagedResourcesAssembler<User> pagedResourcesAssembler) {
    return ResponseEntity.ok(
        pagedResourcesAssembler.toModel(
            userService.searchUsers(username, pageable), userRegistrationModelAssembler));
  }
}
