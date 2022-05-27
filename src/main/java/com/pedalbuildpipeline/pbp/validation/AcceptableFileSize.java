package com.pedalbuildpipeline.pbp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AcceptableFileSizeValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AcceptableFileSize {
  String message() default "contains files that are too large";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  long maximumSizeInBytes();
}
