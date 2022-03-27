package com.pedalbuildpipeline.pbp.security;

import com.pedalbuildpipeline.pbp.security.jwt.JwtFilter;
import com.pedalbuildpipeline.pbp.security.jwt.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
@Import(SecurityProblemSupport.class)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  private final UserDetailsService userDetailsService;
  private final JwtTokenService jwtTokenService;
  private final SecurityProblemSupport securityProblemSupport;

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService);
  }

  @Override
  @Bean
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf()
        .disable()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .exceptionHandling()
        .authenticationEntryPoint(securityProblemSupport)
        .accessDeniedHandler(securityProblemSupport)
        .and()
        .addFilterAt(new JwtFilter(jwtTokenService), BasicAuthenticationFilter.class)
        .authorizeRequests()
        .antMatchers(HttpMethod.POST, "/api/users/registration")
        .permitAll()
        .antMatchers(HttpMethod.POST, "/api/users/authenticate")
        .permitAll()
        .antMatchers("/v3/api-docs", "/swagger-ui/index.html")
        .permitAll()
        .antMatchers("/swagger-resources/**")
        .permitAll()
        .anyRequest()
        .authenticated();
  }
}
