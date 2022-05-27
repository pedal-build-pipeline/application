package com.pedalbuildpipeline.pbp.validation;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class AcceptableContentTypeValidator
    implements ConstraintValidator<AcceptableContentType, MultipartFile[]> {
  private Set<MediaType> acceptableContentTypes = new HashSet<>();

  @Override
  public void initialize(AcceptableContentType constraintAnnotation) {
    acceptableContentTypes =
        Arrays.stream(constraintAnnotation.acceptableContentTypes())
            .map(MediaType::valueOf)
            .collect(Collectors.toSet());
  }

  @Override
  public boolean isValid(MultipartFile[] value, ConstraintValidatorContext context) {
    if (value == null || value.length == 0) {
      return true;
    }

    boolean valid = true;
    for (int i = 0; i < value.length; ++i) {
      MediaType mediaType = MediaType.valueOf(Objects.requireNonNull(value[i].getContentType()));
      if (!acceptableContentTypes.contains(mediaType)) {
        context.disableDefaultConstraintViolation();
        context
            .buildConstraintViolationWithTemplate(
                mediaType
                    + " is not within the list of allowed media types "
                    + acceptableContentTypes)
            .addPropertyNode("mediaType")
            .inIterable()
            .atIndex(i);
      }
    }

    return valid;
  }
}
