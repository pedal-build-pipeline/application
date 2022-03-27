package com.pedalbuildpipeline.pbp.notification.model.email;

import java.util.Optional;

public record EmailAddress(Optional<String> name, String address) {
  public String toRFC822() {
    return name.map((nameValue) -> String.format("%s <%s>", nameValue, address)).orElse(address);
  }
}
