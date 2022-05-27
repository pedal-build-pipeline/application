package com.pedalbuildpipeline.pbp.validation;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AcceptableFileSizeValidator
    implements ConstraintValidator<AcceptableFileSize, MultipartFile[]> {
  private long maxSizeInBytes;

  @Override
  public void initialize(AcceptableFileSize constraintAnnotation) {
    maxSizeInBytes = constraintAnnotation.maximumSizeInBytes();
  }

  @Override
  public boolean isValid(MultipartFile[] value, ConstraintValidatorContext context) {
    if (value == null || value.length == 0) {
      return true;
    }

    boolean valid = true;
    for (int i = 0; i < value.length; ++i) {
      if (value[i].getSize() > maxSizeInBytes) {
        context.disableDefaultConstraintViolation();
        context
            .buildConstraintViolationWithTemplate(
                value[i].getSize() + " is larger than the allowed size of " + maxSizeInBytes)
            .addPropertyNode("contentLength")
            .inIterable()
            .atIndex(i);
      }
    }

    return valid;
  }
}
