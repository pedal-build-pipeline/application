package com.pedalbuildpipeline.pbp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport
@EnableFeignClients
public class PbpApplication {

  public static void main(String[] args) {
    SpringApplication.run(PbpApplication.class, args);
  }
}
