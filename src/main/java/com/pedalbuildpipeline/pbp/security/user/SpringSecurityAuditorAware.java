package com.pedalbuildpipeline.pbp.security.user;

import com.pedalbuildpipeline.pbp.user.repo.entity.User;
import com.pedalbuildpipeline.pbp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SpringSecurityAuditorAware implements AuditorAware<User> {
  private final UserService userService;

  @Override
  public Optional<User> getCurrentAuditor() {
    return Optional.ofNullable(SecurityContextHolder.getContext())
        .map(SecurityContext::getAuthentication)
        .flatMap(authentication -> userService.findByUsername(authentication.getName()));
  }
}
